# AGENTS.md - instructions for AI coding tools

Project: **W.WAW**, a Warsaw ticketing and delay-journal MVP. `backend/` (Java 21, Spring Boot 3.5, H2) and `frontend/` (vanilla HTML/CSS/JS, ES modules) are separate and talk only over the REST API.

## Backend rules
- Layers: `controller` (HTTP only) -> `service` (business logic, `@Transactional`) -> `repository`. Controllers never touch repositories or entities; they return DTO records.
- DTOs are records with Bean Validation annotations and Polish error messages. Entities are never serialized.
- Errors: throw `NotFoundException`, `ConflictException`, `BadRequestException` or `PaymentDeclinedException`; `GlobalExceptionHandler` maps them to the uniform `ApiError` body. Do not return ad-hoc error shapes.
- Time: inject `Clock`, never call `Instant.now()` without it. Business time zone is `Europe/Warsaw` (`AppConfig.WARSAW`).
- Payments go through the `PaymentProvider` interface. Never handle or store card data.
- Delay reports are immutable and expire automatically (`wwaw.delays.retention-days`, default 30). Do NOT add PUT/DELETE endpoints for them; always filter reads by the retention cutoff.
- Keep pure logic (e.g. `StatsCalculator`) free of Spring so it can be unit-tested; add or update tests in `backend/src/test` with every behavior change.
- No Lombok. Prefer records and constructor injection.

## Frontend rules
- No frameworks or build step. One ES module per page in `frontend/js/`, shared code in `api.js` (all fetch calls), `ui.js` (helpers), `layout.js` (header and footer with the site map).
- Always escape user-provided text with `esc()` before putting it into an HTML template string.
- Do not use `form.name` / `form.method` for fields (they are built-in `HTMLFormElement` properties); use `form.elements.namedItem(...)`.
- UI language is Polish; code, identifiers and comments are English.

## Design rules
- Palette only: `#b5ca8d` `#8bb174` `#426b69` `#222e50` `#2a4849` (CSS variables in `css/style.css`).
- Minimal layout: top bar, content, footer with the site map. Keep keyboard focus visible and respect `prefers-reduced-motion`.

## Commands
- Backend: `cd backend && mvn spring-boot:run` (tests: `mvn test`)
- Frontend: `cd frontend && python3 -m http.server 5500`
