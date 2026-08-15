# Pawsulin Web App User Navigation & Flow

## 1) Entry and access control

- **Public routes**: `/login`, `/register`
- **Protected routes**: `/dashboard`, `/pets`, `/pets/:petId`, `/glucose`, `/insulin`, `/alerts` (and `/` / `/home` redirects)
- Unknown paths render a **404 page** with a “Go back home” link to `/` (which redirects authenticated users to `/dashboard`).
- If an unauthenticated user opens a protected route, they are redirected to **`/login`**.
- After successful login, the app returns the user to the originally requested route (or `/dashboard`).
- If an authenticated user opens `/login` or `/register`, they are redirected to **`/dashboard`**.

## 2) Primary navigation structure

After sign-in, users are inside a shared app layout with top navigation:

- **Dashboard** (`/dashboard`)
- **Pets** (`/pets`)
- **Glucose** (`/glucose`)
- **Insulin** (`/insulin`)
- **Alerts** (`/alerts`)
- **Logout** returns user to `/login`
- **Notification bell** opens/closes a panel (overlay), not a separate route

## 3) Main user journeys

### Authentication journey
1. User opens `/login` or `/register`.
2. User signs in/signs up (email/password or enabled social provider).
3. On success:
   - Login → redirect to original destination (or `/dashboard`)
   - Register (email flow) → redirect to `/login` with success message
   - Register/login via social auth → redirect to `/dashboard`

### Pet onboarding journey
1. User opens `/pets`.
2. User creates a pet profile.
3. Pet appears in list and can be opened at `/pets/:petId`.
4. In detail page, user can update or delete profile.
5. Delete action redirects back to `/pets`.

### Glucose tracking journey
1. User opens `/glucose` (or enters via Dashboard “Open glucose tracker” link).
2. User selects pet and time range.
3. User logs glucose reading.
4. User reviews analytics cards, charts, and paginated history.

### Insulin tracking journey
1. User opens `/insulin`.
2. User selects pet.
3. User logs insulin injection.
4. User reviews schedule projection and paginated history.

### Alert configuration journey
1. User opens `/alerts`.
2. User selects pet.
3. User creates threshold alert rules.
4. User reviews/deletes active alerts.
5. Triggered alerts appear in the notification panel (bell).

### Empty-state behavior
- If no pets exist, Dashboard/Glucose/Insulin/Alerts show guidance and a CTA to `/pets`.

## 4) Navigation diagram

```mermaid
flowchart TD
  A[User opens app] --> B{Authenticated?}

  B -- No --> C[/login]
  C --> D{Login success?}
  D -- No --> C
  D -- Yes --> E[Redirect to requested route or /dashboard]

  C -- Create account link --> R[/register]
  R --> R1{Registration method}
  R1 -- Email/password --> C
  R1 -- Social auth --> E

  B -- Yes --> E
  E --> F[/dashboard]

  F --> G[/pets]
  F --> H[/glucose]
  F --> I[/insulin]
  F --> J[/alerts]

  G --> K[/pets/:petId]
  K --> G

  H --> H1[Log reading + view analytics/history]
  I --> I1[Log injection + view schedule/history]
  J --> J1[Create/delete alert rules]

  F -. Header bell opens .-> N[Notification bell panel]

  F --> L{Has pets?}
  H --> L
  I --> L
  J --> L
  L -- Yes --> P[Continue on current page]
  L -- No --> G

  F --> M[Logout]
  M --> C

  X[Unknown route *] --> Y[/404 page]
  Y --> Z[/]
  Z --> B
```
