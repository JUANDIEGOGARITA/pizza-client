package com.example.app.pizzaapp.datamanager;

/**
 * Created by amorrell on 10/7/15.
 */
public interface ServiceCallback<T, E> {

    /**
     * This function will be called when the service returns a success.
     * @param response the response object returned by the associated parser.
     */
    void onSuccess(T response);


    /**
     * This function will be called when the service returs an error (400+, 500+ 300+)
     * @param networkError the NetworkError object.
     */
    void onError(E networkError);

    /**
     * use this call back to start any kind of pre execute task for a service.
     * pre execute methods will be called.
     */
    void onPreExecute();
}
