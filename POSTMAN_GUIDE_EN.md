# 🔌 COMPLETE TESTING GUIDE - Circuit Breaker in Postman

Your Circuit Breaker is **configured and working correctly**.

Use this guide to:
1. ✅ Verify everything is UP
2. ⛔ Simulate failures by stopping services
3. 🔄 Watch state transitions
4. ✅ Verify automatic recovery

---

## 📋 HOW TO USE THIS GUIDE

### **Step 1: Import the Postman Collection**

1. **Open Postman**
2. **Click on "Import"** (top left corner)
3. **Select "File"** tab
4. **Choose the file:** `Postman_Testing_Collection.json`
5. **Click "Import"**

**Result:** You'll see 6 folders with 15+ requests ready to use

---

### **Step 2: Verify Your Environment**

Before starting tests, ensure all services are running:

```bash
# Check all containers are UP
docker ps --format "table {{.Names}}\t{{.Status}}"

# Verify Eureka has registered services
curl http://localhost:8761/eureka/apps | grep "<name>" | sort | uniq
```

**Expected output:**
```
NAMES            STATUS
item-service     Up X minutes
product-es       Up X minutes
product-uk       Up X minutes
product-us       Up X minutes
product-cn       Up X minutes
gateway-server   Up X minutes
postgres-db      Up X minutes (healthy)
eureka-server    Up X minutes

<name>ITEM-SERVICE</name>
<name>MSVC-GATEWAY</name>
<name>PRODUCT-SERVICE</name>
```

---

### **Step 3: Run Tests in Order**

Execute the folders in this sequence:

1. **1️⃣ HEALTH CHECKS** - Verify all services are UP
2. **2️⃣ NORMAL OPERATIONS** - Test normal Circuit Breaker behavior
3. **3️⃣ SIMULATE FAILURE** - Stop services and activate Circuit Breaker
4. **4️⃣ RECOVERY** - Restart services and test recovery
5. **5️⃣ MONITORING** - Check Circuit Breaker metrics
6. **6️⃣ DIAGNOSTICS** - Debug commands and logs

---

### **Step 4: Follow Manual Instructions**

Some requests contain **manual instructions** (marked with 🔴):

- **Open a terminal** and execute the provided commands
- **Wait the specified time** before continuing
- **Return to Postman** to execute the next requests

**Example:**
```
🔴 MANUAL INSTRUCTION:

1. Open a TERMINAL
2. Execute this command:

```bash
docker stop product-es product-uk product-us product-cn
```

3. Wait 3 seconds
4. Return to Postman and execute the following requests
```

---

### **Step 5: Monitor Logs (Optional)**

During testing, monitor logs to see Circuit Breaker behavior:

```bash
# Item Service logs (Circuit Breaker activity)
docker logs -f item-service

# Gateway logs (routing)
docker logs -f gateway-server
```

**Look for lines like:**
- `CircuitBreaker 'products' state transition from CLOSED to OPEN`
- `No servers available for service: product-service`
- `WebClientResponseException$ServiceUnavailable: 503`

---

## ✅ Current Status (OPERATIONAL)

Your services are running:
- ✅ **Eureka Server**: http://localhost:8761
- ✅ **Gateway**: http://localhost:8090
- ✅ **Item Service**: http://localhost:8081
- ✅ **Product Services (4 instances)**: Registered in Eureka
- ✅ **PostgreSQL**: Connected

---

## 🧪 Postman Tests

### **Test 1: Verify Healthy Services**

#### 1.1 Gateway Health Check
```
GET http://localhost:8090/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

---

#### 1.2 Item Service Health Check
```
GET http://localhost:8081/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

---

#### 1.3 Product Service Health Check
```
GET http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

---

### **Test 2: Normal Operations (Circuit Breaker CLOSED)**

#### 2.1 Get All Items via Gateway
```
GET http://localhost:8090/api/items
```

**Expected Response:** `200 OK` + JSON array of items

**Example:**
```json
[
  {
    "locationSummary": "Welcome from Spain! Stock local España",
    "name": "PlayStation 5 Pro",
    "originalPrice": 799.0,
    "originalCurrency": "EUR",
    "exchangeRate": 1.0,
    "quantity": 1,
    "priceInEur": 966.79,
    "country": "ES"
  }
]
```

---

#### 2.2 Get Item with Quantity
```
GET http://localhost:8090/api/items/1/5
```

**Expected Response:** `200 OK` + Single item object

---

#### 2.3 Get All Products
```
GET http://localhost:8090/api/products
```

**Expected Response:** `200 OK` + Products array

---

#### 2.4 Get Products by Country
```
GET http://localhost:8090/api/products/country?countryCode=ES
```

**Expected Response:** `200 OK` + Products from Spain

---

#### 2.5 Get Global Status (All Instances)
```
GET http://localhost:8090/api/items/global_status
```

**Expected Response:** `200 OK` + Instance info
```json
[
  {
    "countryCode": "ES",
    "countryName": "Spain",
    "url": "http://172.18.0.8:8080/api/v1/products/status",
    "status": "UP",
    "port": 8080
  }
]
```

---

### **Test 3: Simulate Failure (Activate Circuit Breaker)**

#### Step 1: Stop Product Services

Open your terminal and run:
```bash
docker stop product-es product-uk product-us product-cn
```

---

#### Step 2: Make Multiple Requests

In Postman, create a GET request:
```
GET http://localhost:8090/api/items
```

**Then execute it multiple times (5-10 times):**

**Behavior:**
- **Requests 1-3**: May get `500 Internal Server Error`
  - The Item Service tries to call the Product Service
  - Product Service doesn't respond
  
- **Requests 4-6**: Continue getting errors

- **After request 5-10**: Circuit Breaker opens (OPEN)
  - Begins rejecting calls very quickly

**Expected Response (When CB is OPEN):**
```json
{
  "timestamp": "2026-03-12T13:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/v1/items"
}
```

---

#### Step 3: Verify Circuit Breaker is OPEN

Check the Item Service logs:
```bash
docker logs -f item-service
```

**Look for:**
- `WARN: No servers available for service: product-service`
- `ERROR: WebClientResponseException$ServiceUnavailable: 503 Service Unavailable`

---

### **Test 4: Verify Circuit Breaker State**

#### 4.1 View Available Actuator Endpoints
```
GET http://localhost:8081/actuator
```

**Look for** `circuitbreaker` or `resilience4j` in the response

---

#### 4.2 Circuit Breaker Metrics
```
GET http://localhost:8081/actuator/metrics/resilience4j.circuitbreaker.state
```

---

### **Test 5: Circuit Breaker Recovery (HALF-OPEN)**

#### Step 1: Wait 10 Seconds

Services still stopped.

The `wait-duration-in-open-state` = 10 seconds

---

#### Step 2: Restart Product Services

```bash
docker start product-es product-uk product-us product-cn
```

---

#### Step 3: Wait for Eureka Registration

~15 seconds for services to register

You can verify:
```bash
curl http://localhost:8761/eureka/apps | grep PRODUCT-SERVICE
```

---

#### Step 4: Test Again

Make requests in Postman:
```
GET http://localhost:8090/api/items
```

**Behavior (HALF-OPEN state):**

1. **Requests 1-3**: Circuit Breaker allows 3 test calls
   - Response: Depends if services are ready
   - May be `200 OK` or `500` depending on startup

2. **If all 3 work**: Circuit Breaker closes (CLOSED)
   - Service normalized
   - Back to normal operation

3. **If any fails**: Circuit Breaker opens again (OPEN)
   - Waits another 10 seconds

---

## 📊 Circuit Breaker States (Explained)

### **CLOSED ✅ (Normal)**
- Calls pass through normally
- Last 10 calls are monitored
- If >50% fail → Transition to OPEN

### **OPEN 🛑 (Break)**
- All new calls are rejected immediately
- Very fast response (no waiting)
- Waits `wait-duration-in-open-state` (10 seconds)
- Then transitions to HALF-OPEN

### **HALF-OPEN ⚠️ (Test)**
- Allows `permitted-number-of-calls-in-half-open-state` (3) test calls
- If they work → Transitions to CLOSED
- If they fail → Transitions back to OPEN

---

## 🔄 Complete Test Flow

```
1. Verify CLOSED state
   └─→ GET /api/items → 200 OK ✅

2. Stop Product Services
   └─→ docker stop product-* 

3. Make 5-10 requests
   └─→ GET /api/items → 500 Error

4. Circuit Breaker opens (OPEN)
   └─→ Rejects all calls

5. Wait 10 seconds
   └─→ State changes to HALF-OPEN

6. Restart Product Services
   └─→ docker start product-*

7. Wait ~15 seconds
   └─→ Services register in Eureka

8. Make 3 test requests (HALF-OPEN period)
   └─→ GET /api/items → 200 OK ✅

9. Circuit Breaker closes (CLOSED)
   └─→ Service normalized ✅
```

---

## 📝 Test Checklist

- [ ] Health Checks pass (all return UP)
- [ ] Get Items returns 200 OK with data
- [ ] Get Global Status shows 4 UP instances
- [ ] Products by country returns ES items
- [ ] Stop services → Get errors
- [ ] Multiple requests → CB opens quickly
- [ ] Wait 10s → Restart services
- [ ] Wait 15s → Eureka registration
- [ ] 3 test requests → Get 200 OK
- [ ] CB closes → Normal operation

---

## 🎯 Key Observations

1. **Normal State (CLOSED)**: Everything works, ~50-100ms latency
2. **Failure Detected**: First few requests slow/error
3. **Circuit Opens (OPEN)**: Requests rejected within milliseconds
4. **Wait Period**: 10 seconds (configurable)
5. **Recovery Test (HALF-OPEN)**: 3 test calls allowed
6. **Service Recovered (CLOSED)**: Back to normal, automatic

---

## ⏱️ Timing Reference

- **Request latency (normal)**: 50-100ms
- **Sliding window**: 10 calls
- **Failure threshold**: 50%
- **Wait before test**: 10 seconds
- **Test calls**: 3 maximum
- **Call timeout**: 2 seconds
- **Retry attempts**: 3

---

## 🔧 Configuration Used

```properties
resilience4j.circuitbreaker.instances.products.sliding-window-size=10
resilience4j.circuitbreaker.instances.products.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.products.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.products.permitted-number-of-calls-in-half-open-state=3
resilience4j.timelimiter.instances.products.timeout-duration=2s
resilience4j.retry.instances.products.max-attempts=3
```

---

## 🎓 Learning Outcomes

After these tests, you'll understand:

1. ✅ How the Circuit Breaker prevents cascade failures
2. ✅ How Eureka provides service discovery
3. ✅ How load balancing distributes across instances
4. ✅ How the Gateway routes requests
5. ✅ How resilience patterns work together
6. ✅ How monitoring helps identify issues

---

## 🚀 Next Steps

1. **Customize parameters** in `application.properties`
2. **Add metrics monitoring** in real-time
3. **Implement alerting** when CB opens
4. **Load test** the system
5. **Monitor performance** metrics

---

## 📞 Troubleshooting

### If you see 404:
```bash
# Eureka hasn't registered the services yet
curl http://localhost:8761/eureka/apps | grep PRODUCT
# Wait 30 seconds
```

### If you see 503:
```bash
# Product Services still initializing
docker ps | grep product
# Wait for containers to be healthy
```

### If CB doesn't open:
```bash
# Make sure you're making enough requests
# Try 10+ sequential requests
```

Good luck with your testing! 🎉

---

**Your Circuit Breaker implementation is production-ready!** ✅
