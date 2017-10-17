<%--
  Created by IntelliJ IDEA.
  User: ouhikoshin
  Date: 2017/10/15
  Time: 下午3:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@include file="../include/admin/adminHeader.jsp" %>
<%@include file="../include/admin/adminNavigator.jsp" %>
<html>
<head>
    <title>Title</title>

    <script>
        $(function () {
            $("#editForm").submit(function() {
                if(!checkEmpty("name","分类名称")){
                    return false;
                }
                return true;
            })
        })
    </script>

</head>
<body>

<div class="panel panel-warning editDiv">
    <div class="panel-heading">编辑分类</div>
    <div class="panel-body">
        <form method="post" id="editForm" action="admin_category_update" enctype="multipart/form-data">
            <table class="editTable">
                <tr>
                    <td>
                        分类名称
                    </td>
                    <td>
                        <input type="text" id="name" name="name" value="${c.name}" class="form-control">
                        <input type="hidden" name="id" value="${c.id}">
                    </td>
                </tr>
                <tr>
                    <td>分类图片</td>
                    <td>
                        <input id="categoryPic" accept="image/gif,image/png,image/jpeg,image/jpg,image/bmp"
                               type="file" name="filepath"/>
                    </td>
                </tr>
                <tr class="submitTR">
                    <td colspan="2" align="center">
                        <button type="submit" class="btn btn-success">提 交</button>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

</body>
</html>
