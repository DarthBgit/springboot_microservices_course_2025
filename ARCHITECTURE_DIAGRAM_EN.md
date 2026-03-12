# 🏗️ MICROSERVICES ARCHITECTURE - Visual Diagram

## 📊 Request Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CLIENT / POSTMAN                                  │
└─────────────────────┬───────────────────────────────────────────────────────┘
                      │
                      │ GET /api/items
                      │ GET /api/products
                      ↓
        ┌─────────────────────────────────┐
        │    SPRING CLOUD GATEWAY         │
        │   (Port 8090)                   │
        │ ──────────────────────────────  │
        │ Route: /api/items/** → item-s. │
        │ Route: /api/products/** → prod │
        │                                 │
        │ Filter: StripPrefix=1          │
        │ Filter: PrefixPath=/api/v1     │
        └──────┬──────────────┬───────────┘
               │              │
        ┌──────▼──────┐  ┌────▼───────────┐
        │ ITEM        │  │ PRODUCT        │
        │ SERVICE     │  │ SERVICES       │
        │(8081)       │  │(Multiple)      │
        │             │  │                │
        │ /api/v1/    │  │ /api/v1/       │
        │ items       │  │ products       │
        └──────┬──────┘  │                │
               │         │ ┌────────────┐ │
               │         ├─│ Spain (ES) │ │
               │         │ └────────────┘ │
               │         │ ┌────────────┐ │
               │         ├─│   UK       │ │
               │         │ └────────────┘ │
               │         │ ┌────────────┐ │
               │         ├─│   US       │ │
               │         │ └────────────┘ │
               │         │ ┌────────────┐ │
               │         └─│ China (CN) │ │
               │           └────────────┘ │
               │                          │
               │  Circuit Breaker Check:  │
               │  ┌────────────────────┐  │
               │  │ 🔌 Resilience4j    │  │
               │  │ Retry: 3 attempts  │  │
               │  │ CB: 50% fail→Open │  │
               │  │ Timeout: 2 sec     │  │
               │  └────────────────────┘  │
               │                          │
               └──────────┬───────────────┘
                          │
                  ┌───────▼────────┐
                  │  LOAD BALANCER │
                  │  (Eureka LB)   │
                  │                │
                  │ Routes to:     │
                  │ - product-es   │
                  │ - product-uk   │
                  │ - product-us   │
                  │ - product-cn   │
                  └───────┬────────┘
                          │
                ┌─────────▼──────────┐
                │  SERVICE DISCOVERY  │
                │   EUREKA SERVER     │
                │   (Port 8761)       │
                │                     │
                │ Registers:          │
                │ ✓ PRODUCT-SERVICE   │
                │ ✓ ITEM-SERVICE      │
                │ ✓ MSVC-GATEWAY      │
                └─────────┬───────────┘
                          │
        ┌─────────────────▼──────────────────┐
        │      POSTGRESQL DATABASE           │
        │       (Port 5432)                  │
        │                                    │
        │ Tables:                            │
        │ ├─ products (All data)             │
        │ └─ currency_rates (Conversion)     │
        └────────────────────────────────────┘
```

---

## 🔌 Circuit Breaker States

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    CIRCUIT BREAKER STATE MACHINE                         │
└─────────────────────────────────────────────────────────────────────────┘

                         ╔═══════════════════╗
                         ║   CLOSED ✅       ║
                         ║  (Operating)      ║
                         ╚═════════╤═════════╝
                                   │
                   ┌───────────────┴───────────────┐
                   │                               │
            Monitors                          Fails >50%
            10 calls                        of 10 last
                   │                               │
                   │                      ╔════════▼═════════╗
                   │                      ║   OPEN 🛑        ║
                   │                      ║ (Circuit Break)  ║
                   │                      ╚════════╤═════════╝
                   │                               │
                   │              Waits 10 seconds
                   │             (wait-duration...)
                   │                               │
                   │                      ╔════════▼════════════╗
                   │                      ║  HALF-OPEN ⚠️       ║
                   │                      ║ (3 test calls)     ║
                   │                      ╚════════╤════════════╝
                   │                               │
                   └───────────────┬───────────────┘
                                   │
                    ┌──────────────┴──────────────┐
                    │                             │
              3 calls OK             3 calls FAIL
              (Recovered)             (Still down)
                    │                             │
                    └──────────────┬──────────────┘
                                   │
                         ┌─────────┴──────────┐
                         │                    │
                    Closes CB            Opens CB
                         │                    │
                         ↓                    ↓
                     CLOSED ✅           OPEN 🛑
                     (Normal)          (Wait 10s)
```

---

## 🔄 Complete Request Flow

```
1. INPUT: GET /api/items (via localhost:8090)
   └─→ Arrives at GATEWAY

2. GATEWAY (Port 8090)
   ├─→ Predicate: Path=/api/items/**? ✓
   ├─→ Filter: StripPrefix=1 (removes /api)
   ├─→ Filter: PrefixPath=/api/v1 (adds /api/v1)
   ├─→ Load Balancer: lb://item-service
   └─→ Final route: http://item-service:8081/api/v1/items

3. ITEM SERVICE (Port 8081)
   ├─→ Controller: /api/v1/items
   ├─→ Handler: getAllItems()
   ├─→ @Retry(name = "products") ← Retries 3 times
   ├─→ @CircuitBreaker(name = "products", fallbackMethod = "..") ← Failure handling
   └─→ Service: findAll()

4. CIRCUIT BREAKER CHECK 🔌
   ├─→ State = CLOSED? → Continue
   ├─→ State = OPEN? → Reject (fast)
   └─→ State = HALF-OPEN? → Allow 3 attempts

5. WEBCLIENT CALL
   ├─→ URL: http://product-service/api/v1/products
   ├─→ Eureka Load Balancer resolves "product-service"
   │   └─→ Finds: product-es, product-uk, product-us, product-cn
   │   └─→ Randomly picks one (e.g: product-es)
   ├─→ Connects to http://172.18.0.8:8080/api/v1/products
   └─→ GET http://172.18.0.8:8080/api/v1/products

6. PRODUCT SERVICE
   ├─→ Receives GET /api/v1/products
   ├─→ Queries DATABASE
   ├─→ Returns JSON with products
   └─→ HTTP 200 OK

7. DATA MAPPING
   ├─→ Product → ItemDTO (automatic mapping)
   ├─→ Applies business logic (currency conversion)
   ├─→ Adds location information
   └─→ Returns ItemDTO list

8. RESPONSE
   ├─→ Item Service sends JSON to Gateway
   ├─→ Gateway relays to client
   └─→ CLIENT receives: 200 OK + JSON

TOTAL TIME: ~50-200ms (if everything is UP)
```

---

## 📊 Ports Table

```
┌─────────────────────┬────────────┬──────────────┬──────────────────┐
│ Service             │ Int Port   │ Host Port    │ URL               │
├─────────────────────┼────────────┼──────────────┼──────────────────┤
│ Eureka Server       │ 8761       │ 8761         │ http://loc:8761   │
│ Gateway             │ 8080       │ 8090         │ http://loc:8090   │
│ Item Service        │ 8081       │ 8081         │ http://loc:8081   │
│ Product Spain       │ 8080       │ 8080         │ http://loc:8080   │
│ Product UK          │ 8080       │ 8082         │ http://loc:8082   │
│ Product USA         │ 8080       │ 8083         │ http://loc:8083   │
│ Product China       │ 8080       │ 8084         │ http://loc:8084   │
│ PostgreSQL          │ 5432       │ 5432         │ jdbc://loc:5432   │
└─────────────────────┴────────────┴──────────────┴──────────────────┘
```

---

## 🌐 Docker Network Topology

```
┌──────────────────────────────────────────────────────────────┐
│                    DOCKER NETWORK                             │
│                   (micro-network)                             │
│                                                               │
│  ╔════════════════════════════════════════════════════════╗ │
│  ║           DOCKER CONTAINERS                           ║ │
│  ║                                                        ║ │
│  ║  ┌────────────────┐                                  ║ │
│  ║  │ eureka-server  │ (172.18.0.2)                     ║ │
│  ║  └────────────────┘                                  ║ │
│  ║  ┌────────────────┐                                  ║ │
│  ║  │ postgres-db    │ (172.18.0.3)                     ║ │
│  ║  └────────────────┘                                  ║ │
│  ║  ┌────────────────┐                                  ║ │
│  ║  │ gateway-server │ (172.18.0.4)                     ║ │
│  ║  └────────────────┘                                  ║ │
│  ║  ┌────────────────┐  ┌──────────────┐                ║ │
│  ║  │ product-es     │  │ product-uk   │ (172.x.x.x)   ║ │
│  ║  └────────────────┘  └──────────────┘                ║ │
│  ║  ┌──────────────┐  ┌──────────────┐                  ║ │
│  ║  │ product-us   │  │ product-cn   │ (172.x.x.x)    ║ │
│  ║  └──────────────┘  └──────────────┘                  ║ │
│  ║  ┌────────────────┐                                  ║ │
│  ║  │ item-service   │ (172.18.0.9)                     ║ │
│  ║  └────────────────┘                                  ║ │
│  ║                                                        ║ │
│  ╚════════════════════════════════════════════════════════╝ │
│                                                               │
│  INTERNAL COMMUNICATION:                                     │
│  • product-es → http://postgres-db:5432                     │
│  • item-service → http://product-service/api/v1/...        │
│  • gateway → http://product-service (via LB)                │
│  • all registered in → http://eureka-server:8761            │
│                                                               │
│  ACCESS FROM HOST (your machine):                           │
│  • Gateway: http://localhost:8090                          │
│  • Eureka: http://localhost:8761                           │
│  • Products: http://localhost:8080-8084                    │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔧 Circuit Breaker Configuration

```
CONFIGURATION IN: Item/src/main/resources/application.properties

┌──────────────────────────────────────────────────────────────┐
│ PARAMETER                          │ VALUE    │ MEANING      │
├──────────────────────────────────────────────────────────────┤
│ sliding-window-size                │ 10       │ Monitors     │
│                                    │          │ 10 calls     │
├──────────────────────────────────────────────────────────────┤
│ failure-rate-threshold             │ 50%      │ If >50%      │
│                                    │          │ fail         │
│                                    │          │ → OPEN       │
├──────────────────────────────────────────────────────────────┤
│ wait-duration-in-open-state        │ 10s      │ Wait before  │
│                                    │          │ testing      │
│                                    │          │ (HALF-OPEN)  │
├──────────────────────────────────────────────────────────────┤
│ permitted-number-of-calls-         │ 3        │ Max 3 calls  │
│ in-half-open-state                 │          │ in           │
│                                    │          │ HALF-OPEN    │
├──────────────────────────────────────────────────────────────┤
│ timelimiter timeout-duration       │ 2s       │ Timeout:     │
│                                    │          │ if >2s       │
│                                    │          │ is failure   │
├──────────────────────────────────────────────────────────────┤
│ retry max-attempts                 │ 3        │ Retries      │
│                                    │          │ if fail      │
└──────────────────────────────────────────────────────────────┘
```

---

## 📈 Key Metrics

```
MONITORING THE CIRCUIT BREAKER:

State          │ Behavior             │ Latency     │ Error Rate
────────────────┼──────────────────────┼─────────────┼──────────
CLOSED ✅       │ Passes calls         │ Normal      │ <50%
OPEN 🛑         │ Rejects calls        │ Very fast   │ 100%
HALF-OPEN ⚠️    │ Allows 3 tests       │ Normal      │ Variable

RECOVERY TIMELINE:

T=0s   ├─ Failure detected
       │
T=1s   ├─ Automatic retry (max 3 times)
       │
T=2s   ├─ Failure persists → Circuit opens (OPEN)
       │
T=12s  ├─ wait-duration-in-open-state = 10s
       │
T=12s  ├─ Transition to HALF-OPEN
       │
T=12s  ├─ Allows 3 test calls
       │
T=13s  ├─ If OK → Circuit closes (CLOSED)
       │
T=13s  └─ Service normalized ✅
```

---

## ✨ Visual Summary

```
BEFORE (WITHOUT CIRCUIT BREAKER):
┌─────────┐  Failure   ┌──────────┐
│ Client  │ ────────→ │ Service  │ Cascade crash
└─────────┘           │  Down    │ → Client waits
                      └──────────┘   until timeout

NOW (WITH CIRCUIT BREAKER):
┌─────────┐          ┌──────────────┐  Failure   ┌──────────┐
│ Client  │ ────────→│  CB: CLOSED  │ ────────→ │ Service  │
└─────────┘          └──────────────┘           │   OK     │
                                                └──────────┘

           If fails >50%:

┌─────────┐          ┌──────────────┐  
│ Client  │ ────────→│  CB: OPEN    │ Rejects fast
└─────────┘          │  (Fallback)  │ Immediate response
                     └──────────────┘  

           After 10s:

┌─────────┐          ┌──────────────┐  Test    ┌──────────┐
│ Client  │ ────────→│ CB: HALF_OPEN│ ────────→│ Service  │
└─────────┘          │  (3 attempts)│          │ (OK?)    │
                     └──────────────┘          └──────────┘
                          │
                  If OK → Closes
                  If Fail → Opens
```

Your architecture is ready for production! 🚀

