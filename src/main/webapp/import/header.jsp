<%@ page contentType="text/html;charset=UTF-8" %>

<header class="main-header">
	<div class="fsec">
		<a href="${pageContext.request.contextPath}/">Home</a>
		<a href="${pageContext.request.contextPath}/biglietti.jsp">Biglietti</a>
		<%
			boolean admin = true; //sono sempre admin in debug
			if (admin) {
				%><a href="${pageContext.request.contextPath}/admin.jsp">Admin</a>
			<%}
		%>
	</div>
</header>