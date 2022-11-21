# Simple Webflux Project

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![Project CI](https://github.com/paulushcgcj/simple-webflux/actions/workflows/ci.yml/badge.svg)](https://github.com/paulushcgcj/simple-webflux/actions/workflows/ci.yml) 
![Coverage](.github/badges/jacoco.svg) 
![Branches](.github/badges/branches.svg)

[![DeepSource](https://deepsource.io/gh/paulushcgcj/simple-webflux.svg/?label=active+issues&show_trend=true&token=FNRQx42bX3GBGa6s_xHQMdb7)](https://deepsource.io/gh/paulushcgcj/simple-webflux/?ref=repository-badge)
[![DeepSource](https://deepsource.io/gh/paulushcgcj/simple-webflux.svg/?label=resolved+issues&show_trend=true&token=FNRQx42bX3GBGa6s_xHQMdb7)](https://deepsource.io/gh/paulushcgcj/simple-webflux/?ref=repository-badge)
[![CodeFactor](https://www.codefactor.io/repository/github/paulushcgcj/simple-webflux/badge/main)](https://www.codefactor.io/repository/github/paulushcgcj/simple-webflux/overview/main)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)

**We use [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/)** so stick with it.

Feel free to copy and alter anything I've coded here, the main purpose of this repository is to serve as a template and reference for something you're doing. Check our [changelog](CHANGELOG.md) file to find out what was implemented.

    If you're not sure on how to grab a copy of something you've seen here, try checking old commits to see how it was and how it is now, that should help you understand my disturbed way of thinking things out.

## Features so far

Here is a summarized feature list of what is already implemented or is part of out "road map". Please keep in mind that so far I'm not documenting anything, but I plan to do it in the future.

- [x] Github Actions
- [x] SonarCloud
- [x] Auto Changelog
- [x] Docker Release
- [x] Project Reactor
- [x] Webflux.fn
- [x] r2dbc with Postgres
- [x] Testcontainer
- [x] Actuator
- [x] Validator
- [x] Metrics
- [x] Tracing
- [x] BDD with Cucumber
- [x] Security with OAuth2 and JWT (at the moment using it from Keycloak)
- [x] Consul
- [ ] Remote Configuration
- [ ] Cache

Also we will introduce some other services to the stack to act as external services. With that we will also introduce:

- [ ] LoadBalancing
- [ ] Kubernetes
- [ ] Environment Deployment


## How to run it

To run using the image, run the following command

```sh

docker run -it --rm \
    -p 8080:8080 \
    -e MYSQL_ROOT_PASSWORD=root \
    -e IO_GITHUB_PAULUSHCGCJ_HOST=database \
    -e IO_GITHUB_PAULUSHCGCJ_DATABASE=simple \
    -e IO_GITHUB_PAULUSHCGCJ_USERNAME=simple \
    -e IO_GITHUB_PAULUSHCGCJ_PASSWORD=root \
    -e SPRING_ZIPKIN_ENABLED=false \
    ghcr.io/paulushcgcj/simple-webflux

```

## How to manage everything

### Keycloak

To export your current Banter realm, execute the following command:

```shell
docker exec -it infra-keycloak-1 /opt/keycloak/bin/kc.sh export \
--dir /tmp/export \
--realm Banter \
--users realm_file
```

Remember to lookup inside the exported realm from type: js to type: resource