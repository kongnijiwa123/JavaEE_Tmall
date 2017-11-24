<%--
  Created by IntelliJ IDEA.
  User: ouhikoshin
  Date: 2017/11/22
  Time: 下午2:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<div >
    <a href="${contextPath}">
        <img id="simpleLogo" class="simpleLogo" src="img/site/simpleLogo.png">
    </a>

    <form action="foresearch" method="post" >
        <div class="simpleSearchDiv pull-right">
            <input type="text" placeholder="平衡车 原汁机" name="keyword">
            <button class="searchButton" type="submit">搜天猫</button>
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
    <div style="clear:both"></div>
</div>
</html>
