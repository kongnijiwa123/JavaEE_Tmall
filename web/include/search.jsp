<%--
  Created by IntelliJ IDEA.
  User: ouhikoshin
  Date: 2017/11/19
  Time: 下午8:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<a href="${contextPath}">
    <img id="logo" src="img/site/logo.gif" class="logo">
</a>

<form action="foresearch" method="post">
    <div class="searchDiv">
        <input name="keyword" type="text" placeholder="时尚男鞋  太阳镜 ">
        <button type="submit" class="searchButton">搜索</button>
        <div class="searchBelow">
            <c:forEach begin="0" end="3" items="${cs}" var="c" varStatus="st">
                <span>
                    <a href="forecategory?cid=${c.id}">
                            ${c.name}
                    </a>
                    <c:if test="${st.count!=4}">
                        <span>|</span>
                    </c:if>
                </span>
            </c:forEach>
        </div>
    </div>
</form>
</html>
