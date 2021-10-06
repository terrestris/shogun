//  SHOGun, https://terrestris.github.io/shogun/
//
//  Copyright © 2020-present terrestris GmbH & Co. KG
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0.txt
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
var shogunApplicationConfig = {
  appPrefix: '/admin',
  path: {
    base: 'https://localhost',
    configBase: '/formconfigs',
    swagger: '/v2/api-docs',
    user: '/users',
    layer: '/layers',
    imageFile: '/imagefiles',
    appInfo: '/info/app',
    auth: {
      login: '/auth/login',
      logout: '/auth/logout',
      isSessionValid: '/auth/isSessionValid'
    },
    keycloak: {
      base: 'https://[(${KEYCLOAK_HOST})]/auth',
      realm: 'SpringBootKeycloak',
      clientId: 'shogun-app'
    },
    loggers: '/actuator/loggers',
    logfile: '/actuator/logfile',
    logo: '/static/img/shogun_logo.ed603078.png',
    evictCache: '/cache/evict',
    metrics: '/actuator/metrics'
  },
  models: [
    'Application'
  ],
  dashboard: {
    news: {
      visible: false
    },
    statistics: {
      visible: false
    },
    applications: {
      visible: true
    },
    layers: {
      visible: true
    },
    users: {
      visible: true
    }
  },
  navigation: {
    general: {
      applications: {
        visible: true,
        schemas: {
          clientConfig: 'IhkApplicationClientConfig',
          layerTree: 'SHOGunLayerTree',
          layerConfig: 'LayerConfig'
        }
      },
      layers: {
        visible: true,
        schemas: {
          clientConfig: 'SHOGunLayerClientConfig',
          sourceConfig: 'SHOGunLayerSourceConfig',
          features: 'GeoJsonObject'
        }
      },
      users: {
        visible: true
      },
      imagefiles: {
        visible: false
      }
    },
    status: {
      metrics: {
        visible: true
      },
      logs: {
        visible: true
      }
    },
    settings: {
      global: {
        visible: true
      },
      logs: {
        visible: true
      }
    }
  }
};
