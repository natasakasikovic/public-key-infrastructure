<a id="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Unlicense License][license-shield]][license-url]
[![Last Commit](https://img.shields.io/github/last-commit/natasakasikovic/public-key-infrastructure?branch=main&style=for-the-badge)](https://github.com/natasakasikovic/public-key-infrastructure/commits/main)

<div align="center">

  <h1 align="center">PUBLIC KEY INFRASTRUCTURE</h1>

  <p align="center">
    <br />
    <a href="https://github.com/natasakasikovic/public-key-infrastructure/issues/new?labels=bug">Report Bug</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#-about-the-project">About The Project</a>
      <ul>
        <li><a href="#-built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#-getting-started">Getting Started</a>
      <ul>
        <li><a href="#-installation-steps">Installation Steps</a></li>
      </ul>
    </li>
    <li><a href="#-available-roles-and-credentials">Available Roles and Credentials</a></li>
  </ol>
</details>

##  📋 About The Project

This project implements a **Public Key Infrastructure (PKI)**, which includes security protocols for managing digital certificates, public and private keys, and establishing trust relationships.

## 🔧 Built With

This project is built using the following core technologies:

### Backend

  [![Java][Java-img]][Java-url]

  [![Spring Boot][SpringBoot-img]][SpringBoot-url]

  [![Maven][Maven-img]][Maven-url]
  
  [![PostgreSQL][PostgreSQL-img]][PostgreSQL-url]

### Frontend

[![Angular][angular-shield]][angular-url]

[![Angular Material][material-shield]][material-url]

[![TypeScript][typescript-shield]][typescript-url]

[![HTML5][html-shield]][html-url]

[![CSS3][css-shield]][css-url]

## Getting Started

Follow the steps below to set up and run the project locally.

### Prerequisites

Before you begin, ensure you have the following installed:

- Node.js – Version 18 or later (includes npm)
- Angular CLI – Version 18 or later (Install with: `npm install -g @angular/cli`)
- Web browser – Chrome, Firefox, or any modern browser

### Installation Steps

1. Clone the repository

    ```sh
    git clone https://github.com/natasakasikovic/public-key-infrastructure.git
    cd public-key-infrastructure
    ```

### Configure backend

1.  Update configuration

    Open the `src/main/resources/application.properties` file and update the necessary values such as:
    - Database connection
    - Mail credentials
    - Any other environment-specific settings

2.  Build the project

    ```sh
    ./mvnw clean install
    ```

3. Run the backend application

    ```sh
    ./mvnw spring-boot:run
    ```

The backend application will start on:
📍 **http://localhost:8080**

### Configure frontend

1. Install dependencies

```sh
npm install
```
2. Start the development server

```sh
ng serve
```

The frontend application will start on:
📍 **http://localhost:4200**


<br/>


## 👥 Available Roles and Credentials

TODO

<p align="right">(<a href="#readme-top">back to top</a>)</p>

[Java-img]: https://img.shields.io/badge/Java-17+-red?logo=java&logoColor=white
[Java-url]: https://www.oracle.com/java/

[SpringBoot-img]: https://img.shields.io/badge/Spring%20Boot-3.3.5-success?logo=springboot
[SpringBoot-url]: https://spring.io/projects/spring-boot

[Maven-img]: https://img.shields.io/badge/Maven-3-blue?logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/

[PostgreSQL-img]: https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/

[contributors-shield]: https://img.shields.io/github/contributors/natasakasikovic/public-key-infrastructure.svg?style=for-the-badge
[contributors-url]: https://github.com/natasakasikovic/public-key-infrastructure/graphs/contributors
[license-shield]: https://img.shields.io/github/license/natasakasikovic/public-key-infrastructure.svg?style=for-the-badge
[license-url]: https://github.com/natasakasikovic/public-key-infrastructure/blob/master/LICENSE.txt
[angular-shield]: https://img.shields.io/badge/Angular-18-red?logo=angular
[angular-url]: https://angular.io/
[material-shield]: https://img.shields.io/badge/Angular%20Material-%23187abd?logo=angular&logoColor=white
[material-url]: https://material.angular.io/
[typescript-shield]: https://img.shields.io/badge/TypeScript-5.5-blue?logo=typescript
[typescript-url]: https://www.typescriptlang.org/
[html-shield]: https://img.shields.io/badge/HTML5-e34f26?logo=html5&logoColor=white
[html-url]: https://developer.mozilla.org/en-US/docs/Web/HTML
[css-shield]: https://img.shields.io/badge/CSS3-1572B6?logo=css3&logoColor=white
[css-url]: https://developer.mozilla.org/en-US/docs/Web/CSS