/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_LAB_USER?: string;
  readonly VITE_LAB_PASSWORD?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
