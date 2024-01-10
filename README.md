# 1.目录结构
    上下文-logic
    上下文-service
    schema
        上下文名称
            aggregations
                聚合编号
                    changelog
                        版本号
                            db1.yaml            数据库升级文件（可多个）
                    mapping
                        map1.jslt               模型映射文件（可多个）
                    rule
                        business
                            dmn1.dmn            业务规则文件（可多个）
                        technology
                            script1.groovy      技术规则文件（可多个）
                            route1.xml
                            dmn1.dmn
                            map1.jslt
                    model.json                  模型说明文件
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