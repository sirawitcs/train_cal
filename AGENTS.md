# train_cal — Agent Guide

## Repo structure

```
train_cal/
├── train_cal_b/          # Spring Boot 4.1.0 / Java 17 backend (Maven)
└── train_cal_f/          # Angular 21.2 standalone frontend (npm/Vite)
```

Both are early-stage skeletons with no business logic beyond scaffolding.

---

## Frontend (`train_cal_f/`)

- **Standalone** — uses `bootstrapApplication`, no `NgModule`. Do NOT set `standalone: true` (default in v20+).
- **Test framework**: Vitest + jsdom via `@angular/build:unit-test`. Run: `npm test` or `ng test`.
- **State**: Angular `signal()` for local state, `computed()` for derived state. Never use `mutate`; use `update` or `set`.
- **Template control flow**: native `@if` / `@for` / `@switch`, not `*ngIf` / `*ngFor` / `*ngSwitch`.
- **No `@HostBinding` / `@HostListener`** — use `host` object in `@Component` decorator.
- **No constructor injection** — use `inject()` function.
- **`.cursor/rules/cursor.mdc`** documents Angular/TS conventions (signals, inputs/outputs functions, OnPush, reactive forms, no `ngClass`/`ngStyle`).
- **Known stale test**: `app.spec.ts` checks for `'Hello, train_cal_f'` but the template renders `'Train Calculator'`.
- **Formatter**: Prettier (printWidth 100, singleQuote, `overrides: [{"files": "*.html", "options": {"parser": "angular"}}]`).
- **EditorConfig**: 2-space indent, utf-8, single quotes for TS.

### Dev commands (run from `train_cal_f/`)

| Action | Command |
|--------|---------|
| Serve dev | `npm start` (serves on `http://localhost:4200`) |
| Build | `npm run build` |
| Test (Vitest) | `npm test` |
| Format | `npx prettier --write src/` |

---

## Backend (`train_cal_b/`)

- **Spring Boot 4.1.0** parent, Java 17, Maven.
- **Only dependency**: `spring-boot-starter-webmvc` (no DB, no security).
- **Build**: `mvnw.cmd` (Maven wrapper for Windows) or `mvnw` (Unix).
- **Config**: `src/main/resources/application.properties` (only `spring.application.name` set).
- **Entrypoint**: `com.example.train_cal_b.TrainCalBApplication`.
- **Test**: `@SpringBootTest` context-loads smoke test via `spring-boot-starter-webmvc-test`.

### Dev commands (run from `train_cal_b/`)

| Action | Command |
|--------|---------|
| Build | `./mvnw.cmd package` |
| Run | `./mvnw.cmd spring-boot:run` |
| Test | `./mvnw.cmd test` (runs all tests via Maven Surefire) |

---

## Conventions worth preserving

- `AGENTS.md` — this file (new).
- `.cursor/rules/cursor.mdc` — Angular/TS conventions (high signal, keep).
- `.cursor/rules/ponytail.mdc` — lazy-senior-dev philosophy (keep if the team uses it).

## Notes

- Both projects are independent — no shared config, no docker-compose, no proxy setup yet.
- No CI/CD, no database, no external services configured.
- The backend serves no endpoints and the frontend makes no HTTP calls.
