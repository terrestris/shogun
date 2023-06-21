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
