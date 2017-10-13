public class test {


    public static void main(String[] args) {
        String uri="/tmall/admin_category_list";
        String contextPath = "/tmall";

        System.out.println(uri);
        uri = uri.replace(contextPath, "");
        System.out.println(uri);

        if (uri.startsWith("/admin_")) {
            String[] paths=uri.split("_");
            String servletPath=paths[1]+"Servlet";
            System.out.println(servletPath);
        }
    }

}

