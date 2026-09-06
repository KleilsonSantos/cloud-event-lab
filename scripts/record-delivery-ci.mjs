#!/usr/bin/env node
/**
 * Record GitHub Actions check conclusions as delivery.ci JSONL events.
 *
 * Usage:
 *   node scripts/record-delivery-ci.mjs --pr 13
 *   node scripts/record-delivery-ci.mjs --workflow-run   # GITHUB_EVENT_PATH set by Actions
 *
 * Local mode appends to .cache/delivery/events.jsonl (gitignored).
 * workflow-run mode writes delivery-ci-events.jsonl for artifact upload.
 */
import { appendFileSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';
import { execFileSync } from 'node:child_process';

function ghJson(args) {
  const out = execFileSync('gh', args, { encoding: 'utf8', maxBuffer: 8 * 1024 * 1024 });
  return JSON.parse(out);
}

function normalizeConclusion(state) {
  const s = String(state || '').toLowerCase();
  if (s === 'success' || s === 'pass' || s === 'completed') return 'success';
  if (s === 'failure' || s === 'fail' || s === 'error') return 'failure';
  if (s === 'skipped' || s === 'skip') return 'skipped';
  if (s === 'cancelled' || s === 'canceled') return 'cancelled';
  if (s === 'timed_out' || s === 'timeout') return 'timed_out';
  if (s === 'pending' || s === 'queued' || s === 'in_progress') return 'pending';
  return s || 'unknown';
}

function isFailureConclusion(conclusion) {
  return conclusion === 'failure' || conclusion === 'cancelled' || conclusion === 'timed_out';
}

function appendJsonl(file, events) {
  if (!events.length) return;
  mkdirSync(dirname(file), { recursive: true });
  for (const ev of events) {
    appendFileSync(file, `${JSON.stringify(ev)}\n`, 'utf8');
  }
}

function buildEvent(input) {
  return {
    kind: 'delivery.ci',
    at: new Date().toISOString(),
    check: input.check,
    conclusion: input.conclusion,
    baseBranch: input.baseBranch,
    headBranch: input.headBranch ?? undefined,
    pr: input.pr ?? undefined,
    runId: input.runId ?? undefined,
    commit: input.commit ?? undefined,
    url: input.url ?? undefined,
    source: input.source,
  };
}

function checksFromPr(prNumber) {
  const pr = ghJson([
    'pr',
    'view',
    String(prNumber),
    '--json',
    'number,baseRefName,headRefName,headRefOid,url',
  ]);
  const checks = ghJson(['pr', 'checks', String(prNumber), '--json', 'name,state,link,workflow']);
  const events = [];
  for (const row of checks) {
    events.push(
      buildEvent({
        check: row.name || row.workflow || 'unknown',
        conclusion: normalizeConclusion(row.state),
        baseBranch: pr.baseRefName || 'unknown',
        headBranch: pr.headRefName || undefined,
        pr: pr.number,
        commit: pr.headRefOid?.slice(0, 7),
        url: row.link || pr.url,
        source: 'gh-cli',
      }),
    );
  }
  return events;
}

function checksFromWorkflowRun() {
  const eventPath = process.env.GITHUB_EVENT_PATH;
  if (!eventPath) throw new Error('GITHUB_EVENT_PATH required for --workflow-run');
  const payload = JSON.parse(readFileSync(eventPath, 'utf8'));
  const run = payload.workflow_run;
  if (!run) throw new Error('workflow_run missing from event payload');

  const repo = run.repository?.full_name || process.env.GITHUB_REPOSITORY;
  const sha = run.head_sha;
  if (!repo || !sha) throw new Error('workflow_run missing repository or head_sha');

  const prNumbers = (run.pull_requests || []).map((p) => p.number).filter(Boolean);
  let baseBranch = run.head_branch || 'unknown';
  if (prNumbers[0]) {
    try {
      const pr = ghJson(['pr', 'view', String(prNumbers[0]), '--json', 'baseRefName']);
      baseBranch = pr.baseRefName || baseBranch;
    } catch {
      // keep head_branch fallback
    }
  }

  const data = ghJson([
    'api',
    `/repos/${repo}/commits/${sha}/check-runs?per_page=100`,
    '--paginate',
  ]);
  const rows = Array.isArray(data)
    ? data.flatMap((p) => p.check_runs || [])
    : data.check_runs || [];

  const events = [];
  for (const row of rows) {
    events.push(
      buildEvent({
        check: row.name || 'unknown',
        conclusion: normalizeConclusion(row.conclusion || row.status),
        baseBranch,
        headBranch: run.head_branch || undefined,
        pr: prNumbers[0],
        runId: run.id,
        commit: sha.slice(0, 7),
        url: row.details_url || run.html_url,
        source: 'github-actions',
      }),
    );
  }
  return events;
}

function main() {
  const args = process.argv.slice(2);
  const prIdx = args.indexOf('--pr');
  const workflowRun = args.includes('--workflow-run');
  let events = [];

  if (prIdx >= 0) {
    const pr = args[prIdx + 1];
    if (!pr) {
      console.error('Usage: record-delivery-ci.mjs --pr <number>');
      process.exit(2);
    }
    events = checksFromPr(pr);
  } else if (workflowRun) {
    events = checksFromWorkflowRun();
  } else {
    console.error('Specify --pr <n> or --workflow-run');
    process.exit(2);
  }

  if (!workflowRun) {
    const metricsFile = join(resolve(process.cwd()), '.cache', 'delivery', 'events.jsonl');
    appendJsonl(metricsFile, events);
    console.log(`Recorded ${events.length} delivery.ci event(s) → ${metricsFile}`);
  } else {
    const artifact = join(process.cwd(), 'delivery-ci-events.jsonl');
    writeFileSync(
      artifact,
      events.map((e) => JSON.stringify(e)).join('\n') + (events.length ? '\n' : ''),
      'utf8',
    );
    console.log(`Wrote ${events.length} delivery.ci event(s) → ${artifact}`);
  }

  const failures = events.filter((e) => isFailureConclusion(e.conclusion));
  if (failures.length) {
    console.log('Failures:', failures.map((f) => `${f.check}(${f.conclusion})`).join(', '));
  }
}

main();
