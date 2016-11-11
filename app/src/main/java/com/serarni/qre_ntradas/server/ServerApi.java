package com.serarni.qre_ntradas.server;

import com.serarni.qre_ntradas.model.Clients;
import com.serarni.qre_ntradas.model.Events;
import com.serarni.qre_ntradas.model.TicketValidationResult;
import com.serarni.qre_ntradas.model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 * Copyright (c) 2016, by Sergio Arnillas.
 */
public interface ServerApi {

    @GET("index.php?controller=Admin&action=login&appmovil=1")
    Call<ServerResponse<User>> authenticate(
            @Query("login_user") String sUser,
            @Query("login_password") String sPassword);

    @GET("index.php?controller=Admin&action=validation&appmovil=1")
    Call<ServerResponse<TicketValidationResult>> validateTicket(
            @Query("id") String id,
            @Query("login") String login);

    @GET("index.php?controller=Admin&action=validation&appmovil=1&salida=1")
    Call<ServerResponse<TicketValidationResult>> reactivateTicket(
            @Query("id") String id,
            @Query("login") String login);

    @GET("index.php?controller=Admin&action=json_events")
    Call<ServerResponse<ArrayList<Events>>> getEvents(
            @Query("username") String id,
            @Query("appmovil") int i);

    @GET("index.php?controller=Admin&action=json_events")
    Call<ServerResponse<ArrayList<Clients>>> getClients(
            @Query("username") String login,
            @Query("event_id") String id,
            @Query("appmovil") int i);

}
