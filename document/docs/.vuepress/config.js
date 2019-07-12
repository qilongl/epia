var nav = require('./nav.js')
var { ComponentNav } = nav

var utils = require('./utils.js')
var { genNav, getComponentSidebar, deepClone } = utils

module.exports = {
  title: 'xml-analysis-component',
  description: '基于xml的解析组件',
  base: '/xml-analysis-component/',
  head: [
    [
      'link',
      {
        rel: 'icon',
        href: '/favicon.ico'
      }
    ]
  ],
  themeConfig: {
    // repo: 'qilongl/lql-tech',//github地址
    // docsRepo: 'qilongl/xml-analysis-component',
    docsDir: 'docs',
    editLinks: true,
    sidebarDepth: 3,
    // algolia: {
    //   //待学习，😁
    //   apiKey: 'ffce0083d0830de5f562c045a481410b',
    //   indexName: 'xml-analysis-component'
    // },
    locales: {
      '/': {
        nav: [
          {
            text: '指南',
            link: '/guide/'
          },
          {
            text: '功能',
            items: genNav(deepClone(ComponentNav), 'ZH')
          }
        ],
        sidebar: {
          '/guide/': [
            {
              title: '开发指南',
              collapsable: false,
              children: genEssentialsSidebar()
            },
            {
              title: '组件',
              collapsable: false,
              children: genAdvancedSidebar()
            },
            {
              title: '常用案例',
              collapsable: false,
              children: genCommonSidebar()
            },
            {
                title: '调用',
                collapsable: false,
                children: genCallSidebar()
              },
            // {
            //   title: '其它',
            //   collapsable: false,
            //   children: [
            //     '/guide/other/faq.md',
            //     '/guide/other/release-notes.md'
            //   ]
            // }
          ],
        //   '/feature/component/': getComponentSidebar(
        //     deepClone(ComponentNav),
        //     'ZH'
        //   ),
        //   '/feature/script/': [
        //     '/zh/feature/script/svgo.md',
        //     '/zh/feature/script/new.md'
        //   ]
        }
      }
    }
  },
  locales: {
    // '/': {
    //   lang: 'en-US',
    //   description: 'A magical vue admin'
    // },
    '/': {
      lang: 'zh-CN',
      description: '基于xml的接口解析组件'
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@public': './public'
      }
    }
  },
  ga: 'UA-109340118-1'
}

function genEssentialsSidebar(type = '') {
  const mapArr = [
    '/guide/',
    '/guide/essentials/root-node.md',
    '/guide/essentials/parameters.md',
    '/guide/essentials/actions.md'
  ]
  return mapArr.map(i => {
    return type + i
  })
}

function genAdvancedSidebar(type = '') {
  const mapArr = [
    '/guide/advanced/select.md',
    '/guide/advanced/insert-update-delete.md',
    '/guide/advanced/var.md',
    '/guide/advanced/service.md',
    '/guide/advanced/rest.md',
    '/guide/advanced/convert.md',
    '/guide/advanced/import.md',
    '/guide/advanced/if.md',
    '/guide/advanced/error.md',
    '/guide/advanced/createfile.md',
    '/guide/advanced/deletefile.md',
    '/guide/advanced/download.md'
  ]
  return mapArr.map(i => {
    return type + i
  })
}

function genCommonSidebar(type = '') {
  const mapArr = [
    '/guide/common/select.md',
    '/guide/common/insert-update-delete.md',
    '/guide/common/var.md',
    '/guide/common/service.md',
    '/guide/common/rest.md',
    '/guide/common/convert.md',
    '/guide/common/import.md',
    '/guide/common/if.md',
    '/guide/common/error.md',
    '/guide/common/createfile.md',
    '/guide/common/deletefile.md',
    '/guide/common/download.md'
  ]
  return mapArr.map(i => {
    return type + i
  })
}

function genCallSidebar(type = '') {
  const mapArr = [
    '/guide/call/get.md',
    '/guide/call/post.md',
  ]
  return mapArr.map(i => {
    return type + i
  })
}
