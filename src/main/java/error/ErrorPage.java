package error;

public class ErrorPage {
    private int code;
    private String massage;

    public ErrorPage(int code, String massage) {
        this.code = code;
        this.massage = massage;
    }

    public ErrorPage() {
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
