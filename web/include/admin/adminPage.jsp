<%@ page language="java" contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8" isELIgnored="false" %>
<html>
<body>

<nav>
    <ul class="pagination">

            <c:choose>
                <c:when test="${page.hasPreviouse}">
                    <li>
                        <%--aria-lable为无障碍标签--%>
                        <a href="?page.start=0${page.param}" aria-label="Previous">
                            <span aria-hidden="true">«</span>
                        </a>
                    </li>

                </c:when>
                <c:otherwise>
                    <li class="disabled">
                        <span aria-hidden="true">«</span>
                    </li>
                </c:otherwise>
            </c:choose>


            <c:choose>
                <c:when test="${page.hasPreviouse}">
                    <li>
                            <%--aria-lable为无障碍标签--%>
                        <a href="?page.start=${page.start-page.count}${page.param}" aria-label="Previous">
                            <span aria-hidden="true">‹</span>
                        </a>
                    </li>

                </c:when>
                <c:otherwise>
                    <li class="disabled">
                        <span aria-hidden="true">‹</span>
                    </li>
                </c:otherwise>
            </c:choose>


        <%--只显示前后3个分页，将end设置为6，并设置3-index--%>
        <c:forEach begin="0" end="6" varStatus="status">
            <%--如果下标的start大于等于0 并且小于等于总页数--%>
            <c:if test="${0<=page.start-page.count*(3-status.index)&&page.start-page.count*(3-status.index)<=(page.totalPage-1)*page.count}">
                <%--如果是当前页，下标不能点击--%>
                <c:choose>
                    <c:when test="${page.start==(page.start-page.count*(3-status.index))}">
                        <li class="disabled current">
                            <span>
                                <fmt:formatNumber type="number" value="${((page.start-page.count*(3-status.index))/page.count)+1}"></fmt:formatNumber>
                            </span>
                        </li>

                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="?page.start=${page.start-page.count*(3-status.index)}${page.param}" aria-label="Next">
                                <span>
                                <fmt:formatNumber type="number" value="${((page.start-page.count*(3-status.index))/page.count)+1}"></fmt:formatNumber>
                                </span>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:forEach>


        <c:choose>
            <c:when test="${page.hasNext}">
                <li>
                    <a href="?page.start=${page.start+page.count}${page.param}" aria-label="Next">
                        <span aria-hidden="true">›</span>
                    </a>
                </li>

            </c:when>
            <c:otherwise>
                <li class="disabled">
                    <span aria-hidden="true">›</span>
                </li>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${page.hasNext}">
                <li>
                    <a href="?page.start=${page.last}${page.param}" aria-label="Next">
                        <span aria-hidden="true">»</span>
                    </a>
                </li>

            </c:when>
            <c:otherwise>
                <li class="disabled">
                        <span aria-hidden="true">»</span>
                </li>
            </c:otherwise>
        </c:choose>

    </ul>
</nav>
</body>
</html>
