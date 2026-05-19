# Practice 7: GitHub Workflow Proposal

This document outlines alternative strategies for the GitHub Actions workflows for Practice 7, focusing on improved test integration, quality gates, and automation.

## Alternative 1: Unified CI/CD Pipeline (Recommended)
This approach combines all stages (build, test, deploy) into a single, cohesive workflow.

### Advantages
- **Consistency:** Ensures that deployment only happens if both unit and integration tests pass.
- **Resource Efficiency:** Avoids redundant checkouts and setups across multiple files.
- **Clear Visualization:** One single graph shows the entire path from code change to deployment.

### Example Configuration (`ci-cd-unified.yml`)
```yaml
name: CI/CD Unified Pipeline

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *' # Daily at midnight

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Run All Tests
        run: ./mvnw verify
      - name: Publish Test Report
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Test Results
          path: '**/target/*-reports/TEST-*.xml'
          reporter: java-junit

  deploy:
    needs: test
    if: github.event_name == 'push' && github.ref == 'refs/heads/main'
    runs-on: self-hosted
    steps:
      - name: Deploy to Kubernetes
        run: |
          # Deployment logic here
```

---

## Alternative 2: Modular Reusable Workflows
Separate logic into small, reusable components.

### Advantages
- **Maintainability:** Fix a bug in the "test" logic once, and it updates everywhere.
- **Flexibility:** Different triggers can reuse the same high-quality test block.

### Structure
- `.github/workflows/reusable-test.yml`: Defines HOW to test.
- `.github/workflows/pr-check.yml`: Calls the test workflow on PRs.
- `.github/workflows/release.yml`: Calls the test workflow, then builds and deploys.

---

## Alternative 3: Trigger-Based Specialized Workflows (Current Improved)
Keep separate files but add cross-checks and scheduling.

### Improvements
- Add a **Scheduled** run to `integrationTest.yml`.
- Add **PR Comments** with coverage summaries.
- Use **Environment Protection Rules** in GitHub to require successful `integrationTest` before `deploy` can run (if using GitHub Enterprise/Pro).

---

## Final Recommendation for Practice 7
Adopt **Alternative 1** but with a slight twist:
- Use **Matrix builds** to test against multiple Java versions (e.g., 21 and 23) if compatibility is a concern.
- Include a **Security Scan** (e.g., Snyk or GitHub CodeQL) as a mandatory step in the pipeline.
- Implement **Auto-merging** for PRs that pass all tests and meet coverage requirements.
