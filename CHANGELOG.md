# [18.0.0](https://github.com/terrestris/shogun/compare/v17.2.0...v18.0.0) (2023-09-18)


### Bug Fixes

* adds missing git-commit-id-maven-plugin in gs-interceptor ([a6b390a](https://github.com/terrestris/shogun/commit/a6b390a3cf477cd9894c220dc638650d79be1899))
* allow extending WebSecurityConfig ([10cb4e4](https://github.com/terrestris/shogun/commit/10cb4e4503c6817017bcfa518395879e048863a7))
* circular dependency problems ([53aa861](https://github.com/terrestris/shogun/commit/53aa86172f1af78b0210bd504b38798b21dad31d))
* cleanup ([2db9a60](https://github.com/terrestris/shogun/commit/2db9a60aacd5e883a9a6691d6d7366f75bafd233))
* extract xmlbind version to property ([e37392f](https://github.com/terrestris/shogun/commit/e37392f75cceeb8b3e93262f73683b17ed64c570))
* fix http proxy tests ([b9e8be4](https://github.com/terrestris/shogun/commit/b9e8be46777c638dac94ff35135da834fab73166))
* fix hypersistence-utils version ([7958976](https://github.com/terrestris/shogun/commit/7958976e6e1cb65d97483990bb6c29a16d911e59))
* fix queryHints import ([7df2511](https://github.com/terrestris/shogun/commit/7df2511cb171dd9b52b1bf435fb78d6dcfa6152b))
* optimize imports ([d0e84f5](https://github.com/terrestris/shogun/commit/d0e84f5148aab56fd622c5893d397b368c45b6b5))
* reenable csrfTokenRequestHandler ([b32f02c](https://github.com/terrestris/shogun/commit/b32f02cf66c98f445275690926c3c36c45a339c8))
* remove commented code ([c3d5f7a](https://github.com/terrestris/shogun/commit/c3d5f7a1cc2c71ed3bd0480c042e97bb6f5a64e4))
* remove newline ([53673e0](https://github.com/terrestris/shogun/commit/53673e0fac288e79094cae9f5f107a860922c839))
* remove not needed hibernate version ([7b0abc4](https://github.com/terrestris/shogun/commit/7b0abc42f1e215a89a96eb672f6d3c87a7204bc4))
* remove unneeded parameter ([4565f49](https://github.com/terrestris/shogun/commit/4565f498025e51a35f095f5a19eaf5e4ed0e88d8))
* reorganize imports ([6fc6f8c](https://github.com/terrestris/shogun/commit/6fc6f8c2d28f70c9b4015f94ff819516b244d4c5))
* set correct creation time for docker images ([8be68e5](https://github.com/terrestris/shogun/commit/8be68e562aa330d8e08138a69164ec9f70c9d71a))
* temporarily allow circular references ([4aefc55](https://github.com/terrestris/shogun/commit/4aefc554273ab2ecb6ff57250d6cfd26d13295e1))
* temproarily allow circular references ([98f731d](https://github.com/terrestris/shogun/commit/98f731d2b711901eb0f42b7ce5584ac46d2a46ce))
* update dependency versions ([e468350](https://github.com/terrestris/shogun/commit/e4683505a14d38aaf985dc0a53c47ff0c8bf2c49))
* update git-commit-id plugin ([104b0b3](https://github.com/terrestris/shogun/commit/104b0b33eb38d2a295fe2bfc6f3f6baeb08e2b16))
* update java base image ([caba9ce](https://github.com/terrestris/shogun/commit/caba9ce66248ec133dc72b679eb25d10b56d319c))
* update to apache httpUtil5 ([8661561](https://github.com/terrestris/shogun/commit/8661561b799877307681d3361e2ced1f9f0ebe95))
* web security setup ([b4979bc](https://github.com/terrestris/shogun/commit/b4979bc61723d4333d6541a49382961ed8d3b972))


### Features

* allow to only override security filterchain ([4b37639](https://github.com/terrestris/shogun/commit/4b37639b233764ad4b88e4c760148d1bbdefa2ca))
* improve content type detection ([4b754e3](https://github.com/terrestris/shogun/commit/4b754e3397088f851483e70bf49537c749f9a5d2))
* include REFERENCE_TABLE and make PropertyFormItemEditConfig abstract ([e4bbaf5](https://github.com/terrestris/shogun/commit/e4bbaf53556f4df767b995072be5d27f18417618))
* update for hibernate 6.1 and hibernate-types ([b105159](https://github.com/terrestris/shogun/commit/b105159c493869b8293acfa639e8be6ba67cddfe))
* update keycloak to 21.0.1 ([f90de77](https://github.com/terrestris/shogun/commit/f90de77515a6cc9a6163789bb7ad9f4bc697bbb5))
* update spring-boot to 3.0.x ([f95fcaf](https://github.com/terrestris/shogun/commit/f95fcaf0b4ef4cbf07c9e7dfc3e9d41d382ae431))


### BREAKING CHANGES

* requires migration for spring / spring-security 6 and hibernate 6 updates

Migration guide:

- update java EE 8 dependencies to jakarta EE 9 (see https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#jakarta-ee)
- update repositories
  - update `@QueryHints` annotations
  - e.g. `org.hibernate.annotations.QueryHints.CACHEABLE` -> `AvailableHints.HINT_CACHEABLE`
  - if you use custom data fetching methods, migration steps for hibernate 6 / 6.1 / 6.2 might be necessary
    - especially consider https://github.com/hibernate/hibernate-orm/blob/6.0/migration-guide.adoc#removals
    - for more information see the hibernate migration guides listed below
- update your SecurityConfigs which extend `KeycloakWebSecurityConfig` or `SimpleWebSecurityConfig`
   - update your filter chain according to the the spring security migration guides below
   - replace `antMatchers` with `requestMatchers`, ignoringAntMatchers with ignoringRequestMatchers
   - update your rules for swagger (`/v3/api-docs`) if they're not already updated
- update your `git-commit-id-plugin` configuration and check if the version set in shogun is overridden (for more information see  https://github.com/terrestris/shogun/pull/730)

# [17.2.0](https://github.com/terrestris/shogun/compare/v17.1.1...v17.2.0) (2023-07-18)


### Bug Fixes

* delete all user/group permissions before deleting the user/group itself ([365f31d](https://github.com/terrestris/shogun/commit/365f31d4e3d18cc75c998c7e169b1d733016f96d))
* generate all args and required args constructor, remove annotations that were dealed by @Data ([554caf0](https://github.com/terrestris/shogun/commit/554caf0c83332408d5224633d7e2d318d56586ec))


### Features

* add WMSTIME layer type ([8d32f1b](https://github.com/terrestris/shogun/commit/8d32f1b78afd83fbe67fd5c91c668209d00c9628))
* adds all args constructor and constructor for required parameters of JSONB models ([70d7192](https://github.com/terrestris/shogun/commit/70d7192e2ea4777177ae90f32ffbdf27b860f927))

## [17.1.1](https://github.com/terrestris/shogun/compare/v17.1.0...v17.1.1) (2023-06-21)


### Bug Fixes

* append the transient provider details for users and groups ([72cb1e6](https://github.com/terrestris/shogun/commit/72cb1e6a1ac20246d4bd8024278ad7f06298d8d6))
* get the actual target entity class and set a common default permission evaluator ([978426f](https://github.com/terrestris/shogun/commit/978426fb784c366bb3eccdf90ba33f5fe0fcf086))

# [17.1.0](https://github.com/terrestris/shogun/compare/v17.0.0...v17.1.0) (2023-06-02)


### Bug Fixes

* fix return value of application findAll() endpoint ([776f813](https://github.com/terrestris/shogun/commit/776f813dada3c918f42285f07d2b39a11c5496ed))
* remove unneeded visible field ([75d3ae9](https://github.com/terrestris/shogun/commit/75d3ae9f21221340bbc89566b8cb38ab71e76d9e))
* replace deprecated usages ([f9b64e1](https://github.com/terrestris/shogun/commit/f9b64e113b24e1566fa5cfd8cd8664c43035a38e))
* set correct example value ([3807a28](https://github.com/terrestris/shogun/commit/3807a28e3c5b0cc6758e5e426ae02a89c00308b4))
* update types ([804ecc7](https://github.com/terrestris/shogun/commit/804ecc740d04a502d9c24d4a4abaeff51165cbb8))


### Features

* add models for search configuration ([d76c8db](https://github.com/terrestris/shogun/commit/d76c8dbb64a27521c8a04b852da29c357ba6b515))
* add models for specifying form configurations(still without i18n support) ([1ad6bba](https://github.com/terrestris/shogun/commit/1ad6bbae028247cc9d2d848c9facf603b807f814))
* adds layer editable flag ([88dd6df](https://github.com/terrestris/shogun/commit/88dd6df89fbdd5e615f7457a994fdace9a9c8071))

# [17.0.0](https://github.com/terrestris/shogun/compare/v16.4.0...v17.0.0) (2023-05-17)


### Bug Fixes

* convert permission query to non native query ([3a151d4](https://github.com/terrestris/shogun/commit/3a151d4b6a9c5b93588df4d924ae3735ba9d0d1e))
* determine read permission id from DB ([ec5ba01](https://github.com/terrestris/shogun/commit/ec5ba01113e24dc09d2d12b5cf0fb1ef582eb875))
* fix api docs for pageable parameter ([11a3a6d](https://github.com/terrestris/shogun/commit/11a3a6da38541ad9070a9b768313a4d15f481e42))
* get base entity class dynamically ([a3c6a35](https://github.com/terrestris/shogun/commit/a3c6a3586ca8f5bc48acb78190e959a62f7bac1c))
* improvements from code review ([4416356](https://github.com/terrestris/shogun/commit/44163562a38c104bca33718a131a559209c1685b))
* move securityExtension to ApplicationConfig ([112dd89](https://github.com/terrestris/shogun/commit/112dd8978796c573fab8c674d01557a0ddfe02dc))
* remove SecurityContextEvaluationExtension ([ce86a05](https://github.com/terrestris/shogun/commit/ce86a0507ef84fe140629f7c17c188c0e53c0acb))
* remove unneeded imports ([7abf3ed](https://github.com/terrestris/shogun/commit/7abf3ed2a338d265a998b4ae7702fe5407f01a96))
* security evaluation extension ([d3437e1](https://github.com/terrestris/shogun/commit/d3437e176b34bc45b44da7ae5235c9764aeda811))
* update tests for new findAll response ([919c0e4](https://github.com/terrestris/shogun/commit/919c0e4682373e9bc64e1a2a73fdd0977b77bffc))


### Features

* add paging and simplify permission check ([c378348](https://github.com/terrestris/shogun/commit/c3783480c90dc1ee9e089c9495a586591b5a9b96))
* also check group permissions ([f887da9](https://github.com/terrestris/shogun/commit/f887da9abaffee71dec8874d7bab24be837c5971))


### BREAKING CHANGES

* changes `BaseController::findAll` signature. Now returns paged entities.

Migration instructions

1. BaseController::findAll() - method signature has changed

- the method now returns `Page<BaseEntity>` instead of `List<BaseEntity>`
- this means the results are now wrapped into a paging object, e.g.:
   ```json
  {
    "content": [
      {
        "id": 475870,
        "created": "2022-10-07T14:01:43.11027Z"
        [...]
      }
    ],
    "pageable": {
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 1,
      "paged": true,
      "unpaged": false
    },
    "last": false,
    "totalElements": 2,
    "totalPages": 2,
    "size": 1,
    "number": 0,
    "sort": {
      [...]
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
   ```
   - `content` contains the list of entities
   - the object also contains metadata, for more information see https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
- the method has a new optional parameter `Pageable pageable` which can be used to specify which page and size is returned e.g.:
   ```json
  {
    "page": 0,
    "size": 10
  }
   ```
- if no pagination configuration is provided, one page with all entities is returned. But it is recommended to use pagination for performance reasons

2. Custom permission evaluators have to implement a `findAll` method

- if you project uses custom permission evaluators, you have to implement this method to provide a way to check permissions for requests with pagination
- BaseEntityPermissionEvaluator contains a default implemenation which performs the new improved permisison check described above
   - the default only works for the "shogun way" (permission managment through UserInstance-, UserClass- GroupInstance- and GroupClassPermissions

# [16.4.0](https://github.com/terrestris/shogun/compare/v16.3.0...v16.4.0) (2023-04-28)


### Bug Fixes

* add JsonInclude.Include.NON_NULL annotations to all jsonb models ([38e4811](https://github.com/terrestris/shogun/commit/38e4811d53b0f38acb2dbeb7a193211f3840677e))
* enable arbitrary objects in open api specification ([563a2ba](https://github.com/terrestris/shogun/commit/563a2babb9194f97457ab030a1c40750a11c7879))
* readd terms of service url ([bb09a79](https://github.com/terrestris/shogun/commit/bb09a79d3c11625ac9927da761e699922582eec3))
* replace deprecated mockito-inline by mockito-core ([16fcbf0](https://github.com/terrestris/shogun/commit/16fcbf0a63169e50e59fd12c37b73c6daa36d18d))


### Features

* add defaultLanguage param to clientConfig ([#685](https://github.com/terrestris/shogun/issues/685)) ([c85dc96](https://github.com/terrestris/shogun/commit/c85dc968ffd5562ef92dad59f77872d869a6fdf1))

# [16.3.0](https://github.com/terrestris/shogun/compare/v16.2.0...v16.3.0) (2023-04-18)


### Features

* introduce crsDefinitions field on mapView config ([7457fb7](https://github.com/terrestris/shogun/commit/7457fb77989fdb8865349e847594139e26f586b9))

# [16.2.0](https://github.com/terrestris/shogun/compare/v16.1.1...v16.2.0) (2023-04-18)


### Features

* extend application client config model for legal information link config ([21d4874](https://github.com/terrestris/shogun/commit/21d487488aa5f182c1b038c9e3627f39c3a8d6d9))
* introduce crsDefinitions field on mapView config ([7457fb7](https://github.com/terrestris/shogun/commit/7457fb77989fdb8865349e847594139e26f586b9))

# [16.2.0](https://github.com/terrestris/shogun/compare/v16.1.1...v16.2.0) (2023-01-20)


### Features

* extend application client config model for legal information link config ([21d4874](https://github.com/terrestris/shogun/commit/21d487488aa5f182c1b038c9e3627f39c3a8d6d9))

## [16.1.1](https://github.com/terrestris/shogun/compare/v16.1.0...v16.1.1) (2023-01-18)


### Bug Fixes

* introduce custom date-time scalar for Instant serialization ([1050d96](https://github.com/terrestris/shogun/commit/1050d966c565a3cdb2e703381e3ec572f016dae8))
* link to original source ([85cf9b2](https://github.com/terrestris/shogun/commit/85cf9b213f4a4b5f86a7ff4780131e916d8e1ea7))
* path to ignore DateTimeScalar from licence check ([504a3c4](https://github.com/terrestris/shogun/commit/504a3c4a2ea72db6b9c73a1bba422e7d3d6fbb4a))
