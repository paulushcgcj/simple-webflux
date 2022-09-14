# Simple Webflux Project

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

[![Project CI](https://github.com/paulushcgcj/simple-webflux/actions/workflows/ci.yml/badge.svg)](https://github.com/paulushcgcj/simple-webflux/actions/workflows/ci.yml) 
![Coverage](.github/badges/jacoco.svg) 
![Branches](.github/badges/branches.svg)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=paulushcgcj_simple-webflux&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=paulushcgcj_simple-webflux)

**We use [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/)** so stick with it.

## How to run it

To run using the image, run the following command

```sh

docker run -it --rm \
    -p 8080:8080 \
    -e MYSQL_ROOT_PASSWORD=root \
    -e IO_GITHUB_PAULUSHCGCJ_HOST=mariadb \
    -e IO_GITHUB_PAULUSHCGCJ_DATABASE=company \
    -e IO_GITHUB_PAULUSHCGCJ_USERNAME=root \
    -e IO_GITHUB_PAULUSHCGCJ_PASSWORD=root \
    -e SPRING_ZIPKIN_ENABLED=false \
    ghcr.io/paulushcgcj/simple-webflux

```