<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: ouhikoshin
  Date: 2017/10/24
  Time: 下午1:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="../include/admin/adminHeader.jsp"%>
<%@ include file="../include/admin/adminNavigator.jsp"%>
<html>
<head>
    <title>编辑产品属性值</title>

    <script>
        $(function () {
            $("input.ptvValue").keyup(function () {
                var value = $(this).val();
                var page="admin_product_updatePropertyValue";
                var ptvid = $(this).attr("ptvid");
                var parentSpan = $(this).parent("span");
                parentSpan.css("border", "2px solid yellow");

                $.post(
                    page,
                    {"value":value,"ptvid":ptvid},
                    function (result) {
                        if("success"==result) {
                            parentSpan.css("border", "2px solid green");
                        }else {
                            parentSpan.css("border", "2px solid red");
                        }
                    }
                )
            })
        })
    </script>

</head>
<body>

<div class="workingArea">
    <ol class="breadcrumb">
        <li>
            <a href="admin_category_list">所有分类</a>
        </li>
        <li>
            <a href="admin_product_list?cid=${p.category.id}">${p.category.name}</a>
        </li>
        <li class="active">${p.name}</li>
        <li class="active">编辑产品属性</li>
    </ol>

    <div class="editPTVDiv">
        <c:forEach items="${ptvs}" var="ptv">
            <div class="eachPTV">
                <span class="ptvName">${ptv.property.name}</span>
                <span class="ptvValue">
                    <input class="ptvValue" ptvid="${ptv.id}" type="text" value="${ptv.value}">
                </span>
            </div>
        </c:forEach>
        <div style="clear:both"></div>
    </div>
</div>

</body>
</html>
