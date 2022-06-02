package winapp.hajikadir.customer.model;

/**
 * Created by user on 20-Jul-16.
 */
public class Parent {
    private static String CategoryId="";
    private static String SubCategoryId="";
    private static boolean isLoading;
    public static String getCategoryId() {
        return CategoryId;
    }

    public static void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public static String getSubCategoryId() {
        return SubCategoryId;
    }

    public static void setSubCategoryId(String subCategoryId) {
        SubCategoryId = subCategoryId;
    }

    public static boolean isLoading() {
        return isLoading;
    }

    public static void setIsLoading(boolean isLoading) {
        Parent.isLoading = isLoading;
    }
}
