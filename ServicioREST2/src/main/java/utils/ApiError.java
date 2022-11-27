package utils;

import com.google.gson.Gson;

public class ApiError {
    private final String error_reason;
    private final int error_code;

    public ApiError(String reason, int code){
        this.error_reason = reason;
        this.error_code = code;
    }

    public static String errorJSON(String reason, int code){
        ApiError error = new ApiError(reason, code);
        return error.toJSON();
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}

