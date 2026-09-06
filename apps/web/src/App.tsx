import { FormEvent, useCallback, useEffect, useState } from "react";

type LabStatus = {
  service: string;
  cloudProvider: string;
  activeProfiles: string;
  honesty: string;
};

type EventRow = {
  id: string;
  type: string;
  source: string;
  status: string;
  idempotencyKey: string;
  createdAt: string;
};

const authHeader = () => {
  const user = import.meta.env.VITE_LAB_USER ?? "lab";
  const pass = import.meta.env.VITE_LAB_PASSWORD ?? "lab-change-me";
  return "Basic " + btoa(`${user}:${pass}`);
};

export default function App() {
  const [status, setStatus] = useState<LabStatus | null>(null);
  const [events, setEvents] = useState<EventRow[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [type, setType] = useState("order.created");
  const [source, setSource] = useState("web-lab");
  const [payload, setPayload] = useState('{"sku":"ABC","qty":1}');

  const refresh = useCallback(async () => {
    setError(null);
    try {
      const [sRes, eRes] = await Promise.all([
        fetch("/api/lab/status"),
        fetch("/api/events?limit=20", { headers: { Authorization: authHeader() } }),
      ]);
      if (!sRes.ok) throw new Error(`status ${sRes.status}`);
      setStatus(await sRes.json());
      if (!eRes.ok) throw new Error(`events ${eRes.status}`);
      setEvents(await eRes.json());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load");
    }
  }, []);

  useEffect(() => {
    void refresh();
    const id = window.setInterval(() => void refresh(), 3000);
    return () => window.clearInterval(id);
  }, [refresh]);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      const res = await fetch("/api/events", {
        method: "POST",
        headers: {
          Authorization: authHeader(),
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          type,
          source,
          idempotencyKey: crypto.randomUUID(),
          payloadJson: payload,
        }),
      });
      if (!res.ok) throw new Error(`ingest ${res.status}`);
      await refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ingest failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main>
      <h1 className="brand">cloud-event-lab</h1>
      <p className="lede">
        Cloud Event Platform — same domain, swappable cloud adapters. Emulators ≠ production
        cloud.
      </p>

      <section className="panel">
        <h2>Lab status</h2>
        {status ? (
          <p>
            provider: <span className="status-ok">{status.cloudProvider}</span> · profiles:{" "}
            <code>{status.activeProfiles}</code>
            <br />
            <small className="status-warn">{status.honesty}</small>
          </p>
        ) : (
          <p className="status-warn">Waiting for API on :8080…</p>
        )}
      </section>

      <section className="panel">
        <h2>Ingest event</h2>
        <form onSubmit={onSubmit}>
          <div className="row two">
            <div>
              <label htmlFor="type">type</label>
              <input id="type" value={type} onChange={(e) => setType(e.target.value)} />
            </div>
            <div>
              <label htmlFor="source">source</label>
              <input id="source" value={source} onChange={(e) => setSource(e.target.value)} />
            </div>
          </div>
          <label htmlFor="payload">payloadJson</label>
          <textarea
            id="payload"
            rows={4}
            value={payload}
            onChange={(e) => setPayload(e.target.value)}
          />
          <button type="submit" disabled={busy}>
            {busy ? "Sending…" : "POST /api/events"}
          </button>
        </form>
        {error && <p className="error">{error}</p>}
      </section>

      <section className="panel">
        <h2>Recent events</h2>
        <table>
          <thead>
            <tr>
              <th>status</th>
              <th>type</th>
              <th>id</th>
            </tr>
          </thead>
          <tbody>
            {events.map((ev) => (
              <tr key={ev.id}>
                <td>{ev.status}</td>
                <td>{ev.type}</td>
                <td>{ev.id.slice(0, 8)}…</td>
              </tr>
            ))}
            {events.length === 0 && (
              <tr>
                <td colSpan={3}>No events yet</td>
              </tr>
            )}
          </tbody>
        </table>
      </section>
    </main>
  );
}
