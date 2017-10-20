<%--
  Created by IntelliJ IDEA.
  User: ouhikoshin
  Date: 2017/10/20
  Time: 上午10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/admin/adminHeader.jsp" %>
<%@ include file="../include/admin/adminNavigator.jsp" %>
<html>
<head>
    <title>编辑产品</title>

    <script>
        $("#editForm").submit(function () {
            if (!checkEmpty("name", "产品名称") || !checkEmpty("subTitle", "产品小标题")
                || !checkEmpty("orignalPrice", "原价格") || !checkEmpty("promotePrice", "优惠价格")
                || !checkEmpty("stock", "库存")) {
                return false;
            }else {
                return true;
            }
        })
    </script>

</head>
<body>

<div class="workingArea">
    <ol class="breadcrumb">
        <li><a href="admin_category_list">所有分类</a></li>
        <li><a href="admin_product_list?cid=${p.category.id}">${p.category.name}</a></li>
        <li class="active">${p.name}</li>
        <li class="active">编辑产品</li>
    </ol>

    <div class="panel panel-warning editDiv">
        <div class="panel-heading">编辑产品</div>
        <div class="panel-body">
            <form method="post" id="editForm" action="admin_product_update">
                <table class="editTable">
                    <tr>
                        <td>产品名称</td>
                        <td>
                            <input id="name" name="name" value="${p.name}" type="text" class="form-control">
                        </td>
                    </tr>
                    <tr>
                        <td>产品小标题</td>
                        <td>
                            <input id="subTitle" name="subTitle" value="${p.subTitle}" type="text"
                                   class="form-control"/>
                        </td>
                    </tr>
                    <tr>
                        <td>原价格</td>
                        <td>
                            <input id="orignalPrice" name="orignalPrice" value="${p.orignalPrice}" type="text"
                                   class="form-control"/>
                        </td>
                    </tr>
                    <tr>
                        <td>优惠价格</td>
                        <td>
                            <input id="promotePrice" name="promotePrice" value="${p.promotePrice}" type="text"
                                   class="form-control"/>
                        </td>
                    </tr>
                    <tr>
                        <td>库存</td>
                        <td>
                            <input id="stock" name="stock" value="${p.stock}" type="number" class="form-control"/>
                        </td>
                    </tr>
                    <tr class="submitTR">
                        <td colspan="2" align="center">
                            <input type="hidden" name="id" value="${p.id}">
                            <input type="hidden" name="cid" value="${p.category.id}"/>
                            <button type="submit" class="btn btn-success">提 交</button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>

</body>
</html>
