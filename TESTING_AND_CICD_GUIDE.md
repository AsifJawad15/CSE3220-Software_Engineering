# 🎓 Student Management System - Complete Testing & CI/CD Guide

## 📚 Table of Contents
1. [Project Overview](#project-overview)
2. [Testing Explained](#testing-explained)
3. [CI/CD Pipeline Explained](#cicd-pipeline-explained)
4. [GitHub Workflow & Branch Protection](#github-workflow--branch-protection)
5. [Step-by-Step Setup Instructions](#step-by-step-setup-instructions)

---

## 📋 Project Overview

This is a **Spring Boot application** with:
- **Role-Based Authentication**: STUDENT (read-only) and TEACHER (full CRUD)
- **Spring Security**: Protects URLs based on user roles
- **H2 Database for Testing**: In-memory database for fast, isolated tests
- **PostgreSQL for Production**: Real database in Docker

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  (Controllers: AuthController, StudentController, TeacherController)│
├─────────────────────────────────────────────────────────────────┤
│                        SECURITY LAYER                            │
│  (SecurityConfig: URL protection, Role-based access)            │
├─────────────────────────────────────────────────────────────────┤
│                        SERVICE LAYER                             │
│  (StudentService: READ only, TeacherService: Full CRUD)         │
├─────────────────────────────────────────────────────────────────┤
│                       REPOSITORY LAYER                           │
│  (Spring Data JPA: StudentRepository, UserRepository, etc.)     │
├─────────────────────────────────────────────────────────────────┤
│                        DATABASE LAYER                            │
│  (H2 for tests, PostgreSQL for production)                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🧪 Testing Explained

### What is H2 Database?
- **H2** is an in-memory database that runs entirely in RAM
- No installation needed - perfect for testing
- Creates fresh database for each test run
- Destroyed when tests finish (no cleanup needed)
- GitHub Actions can run tests without setting up PostgreSQL

### Types of Tests Created

#### 1. Unit Tests
- Test ONE class in isolation
- Use mock objects to simulate dependencies
- Very fast (milliseconds)
- Files: `AuthControllerTest.java`

#### 2. Integration Tests  
- Test multiple components working together
- Use real H2 database
- Test actual HTTP requests through Spring Security
- Files: `StudentControllerTest.java`, `TeacherControllerTest.java`, `SecurityConfigTest.java`

#### 3. Service Layer Tests
- Test business logic
- Files: `StudentServiceTest.java`, `TeacherServiceTest.java`, `CustomUserDetailsServiceTest.java`

### Test Files Structure
```
src/test/java/com/example/assignment/
├── controller/
│   ├── AuthControllerTest.java         # Login/logout tests
│   ├── StudentControllerTest.java      # Student role tests
│   └── TeacherControllerTest.java      # Teacher CRUD tests
├── service/
│   ├── StudentServiceTest.java         # Read-only operations
│   ├── TeacherServiceTest.java         # CRUD operations
│   └── CustomUserDetailsServiceTest.java # Authentication tests
├── config/
│   └── SecurityConfigTest.java         # Role-based access tests
└── resources/
    ├── application.properties          # H2 config for tests
    └── application-test.properties     # Test profile config
```

### Running Tests Locally
```bash
# Run all tests
./mvnw test -Dspring.profiles.active=test

# Run specific test class
./mvnw test -Dtest=SecurityConfigTest

# Run with verbose output
./mvnw test -Dspring.profiles.active=test -X
```

---

## 🔄 CI/CD Pipeline Explained

### What is CI/CD?
- **CI (Continuous Integration)**: Automatically test code when pushed
- **CD (Continuous Deployment)**: Automatically deploy after tests pass

### What is GitHub Actions?
- GitHub's built-in automation tool
- Runs on GitHub's servers (free for public repos)
- Triggered by events (push, pull request)

### Our CI/CD Files

#### 1. `.github/workflows/ci.yml`
Main pipeline that runs on every push and PR:
```yaml
Jobs:
1. test     → Run all unit & integration tests
2. build    → Build JAR file (only if tests pass)
```

#### 2. `.github/workflows/pr-checks.yml`
Runs specifically on Pull Requests:
- Validates code compiles
- Runs all tests
- Shows ✅ or ❌ status

### Workflow Trigger Events
| Event | When | What Happens |
|-------|------|--------------|
| `push` | Code pushed to main/testing | Full CI pipeline runs |
| `pull_request` | PR created/updated | PR validation runs |

---

## 🔐 GitHub Workflow & Branch Protection

### Branch Strategy (from screenshot)
```
main                    ← Production branch (protected)
  │
  ├── CI_CD_testing     ← Testing CI/CD changes
  │
  ├── testing           ← Testing features
  │
  └── feature/my-feature← Feature development
```

### What is Branch Protection?
Rules that prevent direct changes to important branches:
- **Require Pull Requests**: No direct pushes to `main`
- **Require Reviews**: Someone must approve before merge
- **Require Status Checks**: CI tests must pass before merge

---

## 🚀 Step-by-Step Setup Instructions

### Step 1: Initialize Git Repository (if not done)
```powershell
cd D:\3.2\SWE\lab\assignment
git init
git add .
git commit -m "Initial commit with testing and CI/CD"
```

### Step 2: Add Remote Repository
```powershell
git remote add origin https://github.com/AsifJawad15/CSE3220-Software_Engineering.git
```

### Step 3: Create and Push Branches
```powershell
# Create main branch and push
git branch -M main
git push -u origin main

# Create testing branch
git checkout -b testing
git push -u origin testing

# Create CI_CD_testing branch
git checkout -b CI_CD_testing
git push -u origin CI_CD_testing

# Create feature branch
git checkout -b feature/my-feature
git push -u origin feature/my-feature

# Go back to main
git checkout main
```

### Step 4: Configure Branch Protection Rules

1. **Go to GitHub Repository Settings**
   - Navigate to: `https://github.com/AsifJawad15/CSE3220-Software_Engineering/settings`

2. **Click "Branches" in left sidebar**

3. **Add Branch Protection Rule for `main`:**
   - Click "Add rule"
   - Branch name pattern: `main`
   - ✅ Check: "Require a pull request before merging"
     - ✅ Check: "Require approvals" (set to 1)
   - ✅ Check: "Require status checks to pass before merging"
     - Search and select: `Run Unit & Integration Tests`
     - Search and select: `Validate PR`
   - ✅ Check: "Require branches to be up to date before merging"
   - ❌ Uncheck: "Allow force pushes"
   - Click "Create"

4. **Add Branch Protection Rule for `CI_CD_testing`:**
   - Click "Add rule"
   - Branch name pattern: `CI_CD_testing`
   - ✅ Check: "Require status checks to pass before merging"
   - Click "Create"

### Step 5: Test the Workflow

1. **Make a change on feature branch:**
```powershell
git checkout feature/my-feature
# Make some changes to a file
git add .
git commit -m "Test feature change"
git push origin feature/my-feature
```

2. **Create Pull Request on GitHub:**
   - Go to repository on GitHub
   - Click "Compare & pull request"
   - Base: `main` ← Compare: `feature/my-feature`
   - Fill in title and description
   - Click "Create pull request"

3. **Watch CI/CD Run:**
   - See the "Checks" tab
   - Wait for ✅ green checkmarks
   - If tests fail ❌, fix code and push again

4. **Request Review:**
   - Add reviewers on right sidebar
   - Wait for approval

5. **Merge PR:**
   - Once approved and tests pass
   - Click "Merge pull request"
   - Click "Confirm merge"

### Step 6: Verify Everything Works

1. Check GitHub Actions tab: `https://github.com/AsifJawad15/CSE3220-Software_Engineering/actions`
2. Should see green ✅ for all workflows
3. Check that `main` branch cannot be pushed to directly

---

## 📖 File Summary

### New Files Created:
| File | Purpose |
|------|---------|
| `src/test/resources/application-test.properties` | H2 database config for tests |
| `src/test/resources/application.properties` | Default test config |
| `.github/workflows/ci.yml` | Main CI/CD pipeline |
| `.github/workflows/pr-checks.yml` | Pull request validation |
| `src/test/java/.../controller/*Test.java` | Controller tests |
| `src/test/java/.../service/*Test.java` | Service tests |
| `src/test/java/.../config/SecurityConfigTest.java` | Security tests |

### Modified Files:
| File | Change |
|------|--------|
| `pom.xml` | Added H2 and Mockito dependencies |

---

## ❓ FAQ

### Q: Why H2 instead of PostgreSQL for tests?
**A:** H2 runs in memory, no Docker needed, tests run faster, perfect for CI/CD.

### Q: Why do some tests show errors locally?
**A:** Java 25 has Mockito compatibility issues. GitHub Actions uses Java 22 which works perfectly.

### Q: How do I know if CI/CD is working?
**A:** Check the "Actions" tab on GitHub. Green ✅ = working, Red ❌ = failing.

### Q: What if tests fail in PR?
**A:** Fix the code locally, commit, push. CI will run again automatically.

---

## 🎯 Quick Commands Reference

```powershell
# Run tests
./mvnw test -Dspring.profiles.active=test

# Build without tests
./mvnw package -DskipTests

# Create new branch
git checkout -b branch-name

# Push branch
git push -u origin branch-name

# Check all branches
git branch -a

# Switch branch
git checkout branch-name
```

---

**Good luck with your assignment! 🚀**
