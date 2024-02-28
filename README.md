# 1.目录结构
    上下文-logic
    上下文-service
    schema                                      模式文件，即元数据目录，schema分支只有该目录
        上下文名称                               （上下文-service编译时，若当前分支不属于main,master,dev,develop,main,hotfix,schema，则会从schema分支获取同名目录覆盖到当前目录） 
            aggregations
                聚合编号
                    changelog
                        版本号
                            db1.yaml            数据库升级文件（可多个）<https://docs.liquibase.com/start/get-started/liquibase-yaml.html>

                    mapping
                        map1.jslt               模型映射文件（可多个）<https://github.com/schibsted/jslt>

                    rule
                        business
                            dmn1.dmn            业务规则文件（可多个）<https://sandbox.kie.org/0.23.0/#/eebbf0fc-55e6-4fdc-98b9-6211b4df7ed0/file/Sample.dmn>
                        technology
                            script1.groovy      技术规则文件（可多个）
                            route1.xml
                            dmn1.dmn
                            map1.jslt
                    model.json                  模型说明文件 <https://openapi.apifox.cn>
            cases
                用例编号
                    protocol
                        api
                            BSLG001.json        业务逻辑协议（一个，BSLG前缀）
                            INVE001.json        交互逻辑协议（可多个，INVE前缀）
                        rpc
                            out
                                DL001.json      交互逻辑协议（可多个，DL前缀）
                            in
                                DL001.json      交互逻辑协议（衍生逻辑提供用例一个，DL前缀）
                        runner
                            BSLG001.json        初始化数据文件（与业务逻辑协议同名）
                    intro.json                  用例说明文件
            intro.json                          上下文说明文件

# 2.openapi自定义属性说明
    路径     属性                        说明
    /
        x-metadata			        元数据
    /info/
        x-aggregation		        当前协议文件所在聚合		eg:aggr000010
        x-service			        service元数据
        x-adapter			        adapter元数据
    /servers/
        x-host				        当前协议的服务提供方		eg:colibri-erp.stores.stores
    /components/schemas/xxxx/
        x-model-type			    模型类型，需创建对应的Java类文件则使用该属性    枚举:entity(根实体),valobj(值对象)
        x-data-type			        数据类型，根实体中引用的值对象的持久化注解生成   枚举:version(版本号),identity(版本号),embedded(嵌套),json(JSON),transient(不持久化)
        x-one-many(成对使用)		    entity一对多的多方模型名称	eg:#/components/schemas/Store
        x-many-one(成对使用)		    entity多对一的多方模型名称	eg:#/components/schemas/Store
        x-one-many-unidirectional	entity一对多的多方模型名称	eg:#/components/schemas/Store
        x-query-rules			    entity查询规则列表		eg:{name:findByName,paramTypes:["#/components/schemas/Store","integer,int64"],resultType:"#/components/schemas/Store"}
    /components/schemas/xxxx/properties/xxxx/
        x-data-type			        数据类型，值对象中属性的持久化注解生成		 枚举:text(文本类型)，其他数据类型使用"type"+"format"标明
    /paths/xxxx/post/requestBody/content/xxxx/
        x-subject-path			    标记当前接口参数中登录用户标识位置，适配多接口用户标记位置不一问题	    eg:/userId

| format  | type  | java type |
|---------|-------|-----------|
| object  |       | Object    |
| string  |       | String    |
| integer |       | Integer   |
| integer | int64 | Long      |
| number  |       | Double    |
| boolean |       | Boolean   |
| array   |       | List      |

# 3.逻辑协议文件模板
    {
        "x-metadata": {
            "portType": "SCHEDULE",                                 // 端口类型必须存在，ADAPTER（默认值），SCHEDULE，RUNNER，LISTENER
            "schedule": {
                "cron": "0 0/3 * * * ?"                             // SCHEDULE类型端口 定时表达式
            },
            "listeners": [
                {        
                    "event": "BusinessAccountNeedRenewalEvent",     // LISTENER类型端口 监听事件
                    "topic": "BusinessAccount"                      // LISTENER类型端口 监听主题
                }
            ],
            "runner": {
                "runnerOrder": "",                                  // RUNNER类型端口   执行器顺序
            },
            "events": [                                             // 发送事件
                {
                    "event": "BusinessAccountNeedRenewalEvent",     // 发送事件名称
                    "topic": "BusinessAccount",                     // 发送主题名称
                    "mappings": [                                      
                        {
                            "receiver": "xx.x.x",                   // 接收方地址（产品线.子域.上下文）
                            "service": "xxxxxService",              // 接收方服务
                            "mappingFile": "MAP000242.jslt",        // 接收方映射文件
                            "batchModel": "xx模型",                  // 标记批量接收的模型
                        }
                    ]
                }
            ],
            "command": {
                "logicType": "DMN",                                 // 逻辑类型
                "logicPath": "",                                    // 脚本路径 
                "entity": "BusinessAccount",                        // 执行实体
                "logicParams": [                                    // 规则参数
                    {
                        "name": "businessAccount",                  // 规则入参 
                        "mappingFile": "MAP000004.jslt"             // 规则入参映射
                    }
                ],
                "logicResults": [                                   // 规则结果
                    {
                        "path": "/target",                          // 规则出参     暂时固定写法/target
                        "mappingFile": "MAP000005.jslt"             // 规则出参映射  
                    }
                ],
                "idPath": "",                                       // ID取值路径 
                "autoCreate": true,                                 // 执行业务时是否自动创建根实体
                "repositoryOrder": "",                              // 查找实体顺序
                "models": [                                         // 命令包含模型
                    {
                        "model": "ShopProfile",                     // 模型名称
                        "concept": "shopProfile",                   // 属性名称 
                        "protocol": "test_xxxx.json",               // 集成协议文件
                        "requestMappingFile": "MAP000245.jslt",     // 请求映射 
                        "responseMappingFile": "",                  // 响应映射
                        "order": "",                                // 集成顺序
                        "batchFlag": false，                        // 集成结果是否为批量
                        "ignoreIfParamAbsent": false,               // 可忽略的集成
                    }
                ]
            },
            "queries": [                                            // 查询  
                {
                    "entity": "BusinessAccount",                    // 查询实体 
                    "paramMappingFiles": [
                        "MAP000253.jslt", "MAP000255.jslt"          // 参数映射
                    ],
                    "concept": "result",                            // 包装返回结果模型
                    "method": "findAll",                            // JPA方法名 
                    "order": ""                                     // 查询顺序
                }
            ],
            "integrations": [                                       // 集成 
                {
                    "protocol": "test.json",                        // 集成协议文件
                    "requestMappingFile": "MAP000250.jslt",         // 请求映射 
                    "responseMappingFile": "",                      // 响应映射
                    "concept": "businessPlatformList",              // 模型名称
                    "order": ""                                     // 集成顺序
                }
            ],
            "generalTechnologies": [                      // 通用技术 
                {
                    "name": "xxx",                            // 模型名称
                    "paramMappingFile": "MAP0000121.jslt",    // 参数映射文件
                    "variableMappingFile": "MAP0000121.jslt", // 变量映射文件
                    "routeContentPath": "a/b",                // 路由内容路径
                    "routeXmlFilePath": "XXXX.xml",           // 路由文件路径
                    "order": ""                              // 执行顺序
                }
            ],
            "specialTechnologies": [   // 专用技术           
                {
                    "name": "xxx",        // 模型名称
                    "paramMappingFile": "MAP0000121.jslt",       // 参数映射文件
                    "variableMappingFile": "MAP0000121.jslt",    // 变量映射文件
                    "scriptContentPath": "a/b",                  // 脚本内容路径
                    "scriptFilePath": "XXXX.xml",                // 脚本文件路径
                    "technologyType": "",    // DMN,GROOVY,JSLT,ROUTE,AUTHENTICATION,DECERTIFICATION,PBKDF2_ENCRYPT,GENERATE_SALT,GENERATE_VERIFY_CODE,COMPARE_VERIFY_CODE,GET_ACTIVE_PROFILES
                    "order": ""             // 执行顺序
                }
            ]
        },
        "openapi": "3.0.3",
        "info": {
            "x-aggregation": "aggr000012",      // 所属聚合必须存在  当前用例的
            "title": "GetBusinessAccountList",  // 业务逻辑/交互逻辑/衍生逻辑 - 英文名称
            "version": "1.0.0",
            "x-service": {
                "permission": "",                // 权限表达式         
                "customContent": "",             // 服务自定义逻辑
                "assembler": "MAP000254.jslt"    // 业务逻辑不填写， 交互逻辑/衍生逻辑结果映射      
            },
            "x-adapter": {
                "customContent": "",            // 适配器自定义逻辑
                "requestMappingFile": "",       // 适配器请求映射   
                "responseMappingFile": ""       // 适配器响应映射     
            }
        },
        "components": {},
        "servers": [
            {
                "x-host": "colibri-erp.accounts.accounts",     //必须存在   固定.子域.上下文.
                "url": "/accounts/accounts/aggr000012"         //必须存在  子域/上下文/所属聚合 当前用例的
            }
        ],
        "paths": {
            "/bslg000093": {                  //
                "post": {                       // 请求方式，固定写法
                    "operationId": "bslg000093",  //
                    "requestBody": {
                        "content": {
                            "application/json": {
                                "examples": {
                                    "default": {
                                        "value": {}
                                    }
                                },
                                "schema": {
                                    "$ref": "#/components/schemas/WindowIdentity"           // 条件模型  #/components/schemas/英文名称 （
                                },
                                "x-validator": {  //在项目下 schema/上下文英文名/aggregations/用例所属聚合/model.json中查看上边找到的模型下有无"x-validators"对象，有则需要写"x-validator"对象，没有则不用写"x-validator"对象
                                    "$ref": "#/components/schemas/[英文名称]/x-validators/normal"
                                }
                            }
                        }
                    },
                    "responses": {
                        "default": {
                            "description": "通道强制同步",    // 描述内容 
                            "content": {
                                "application/json": {
                                    "examples": {
                                        "mock": {// 返回的mock数据  
                                            "value": {
                                                "code": "200",     
                                                "message": "",
                                                "data": {}         
                                            }
                                        },
                                        "default": {
                                            "value": {
                                                "code": "",
                                                "message": "",
                                                "data": {}
                                            }
                                        }
                                    },
                                    "schema": {
                                        "type": "object",
                                        "properties": {
                                            "code": {
                                                "type": "string"
                                            },
                                            "data": {
                                                "$ref": "#/components/schemas/ThirdOrderIdListResult"    // 响应模型 #/components/schemas/英文名称
                                            },
                                            "message": {
                                                "type": "string"
                                            }
                                        }
                                    },
                                    "x-validator": { //在项目下 schema/上下文英文名/aggregations/用例所属聚合/model.json中查看上边找到的模型下有无"x-validators"对象，有则需要写"x-validator"对象，没有则不用写"x-validator"对象
                                        "$ref": "#/components/schemas/xxxxx/x-validators/normal"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }