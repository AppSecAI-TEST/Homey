package app.logic.managers;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.AppController;
import app.logic.lib.Group;
import app.logic.lib.User;
import app.task.Task;
import callback.GroupCallBack;
import callback.GroupsCallBack;
import callback.ServerCallBack;
import callback.TaskCallBack;
import callback.TasksCallBack;
import callback.UserCallBack;
import db.DBDriver;

/**
 * Created by Raz on 12/20/2016.
 */

public class DBManager extends ManagerBase {

    public DBManager() {
        super();
    }

    private DBDriver driver = new DBDriver();

    public void Login(final String email, final String password, final UserCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetLoginURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject userObj = jObj.getJSONObject("user");
                        String name = userObj.getString("name");
                        String email = userObj.getString("email");
                        String created_at = userObj.getString("created_at");

                        // Inserting row in users table
                        User user = new User(name, email, uid, created_at);

                        callBack.onSuccess(user);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to store user in MySQL database. will post params(name,
     * email, password) to register url
     */
    public void RegisterUser(final String name, final String email, final String password, final UserCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetRegistrationURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject userObj = jObj.getJSONObject("user");
                        String name = userObj.getString("name");
                        String email = userObj.getString("email");
                        String created_at = userObj
                                .getString("created_at");

                        User user = new User(name, email, uid, created_at);

                        callBack.onSuccess(user);

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to restore user's password
     */
    public void ResetPassword(final String email, final ServerCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset_pass";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetAPIPassResetURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // pass successfully reset

                        callBack.onSuccess(new JSONObject().put("error", "false"));

                    } else {

                        // Error occurred in reset password. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to reset url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void ChangePassword(final int userId, final String oldPass, final String newPass) {

    }

    public void UpdateUser(final int userId, final String property, final Object value, final ServerCallBack callBack) {

    }

    public void UpdateTask(final int taskId, final String property, final Object value, final ServerCallBack callBack) {

    }

    public void UpdateGroup(final int groupId, final String property, final Object value, final ServerCallBack callBack) {

    }

    public void AddTask(final String name, final String description, final int creatorId, final String status, final String location, final Date startTime, final Date endTime, final TaskCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "add_task";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetAPIAddTaskURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {// Task successfully stored in MySQL
                        String name = jObj.getString("name");
                        String id = jObj.getString("id");
                        String description = jObj.getString("description");
                        String creatorId = jObj.getString("creator_id");
                        String status = jObj.getString("status");
                        String location = jObj.getString("location");
                        Date startTime = new Date(jObj.getString("start_time"));
                        Date endTime = new Date(jObj.getString("end_time"));
                        callBack.onSuccess(new Task(name,description,status,location,Integer.parseInt(creatorId),startTime,endTime));
                    } else {

                        // Error occurred while adding a app.task. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("description", description);
                params.put("creator_id", creatorId + "");
                params.put("status", status);
                params.put("location", location);
                params.put("start_time", new java.sql.Date(startTime.getTime()).toString());
                params.put("end_time", new java.sql.Date(endTime.getTime()).toString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void GetUser(final int userId, final UserCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "req_get_user";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetAPIGetUserURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully pulled from MySQL
                        String uid = jObj.getString("uid");
                        JSONObject userObj = jObj.getJSONObject("user");
                        String name = userObj.getString("name");
                        String email = userObj.getString("email");
                        String created_at = userObj.getString("created_at");

                        User user = new User(name, email, uid, created_at);

                        callBack.onSuccess(user);

                    } else {

                        // Error occurred in getting user. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to getting user url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", userId + "");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void AddGroup(final String name, final byte[] img, final long created, final GroupCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "add_group";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetAPIAddGroupURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {// Group successfully stored in MySQL
                        String name = jObj.getString("name");
                        String id = jObj.getString("id");
                        byte[] img = jObj.getString("img").getBytes();
                        callBack.onSuccess(new Group(name));
                    } else {

                        // Error occurred while adding a group. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to adding group url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("created", Long.toString(created));
                params.put("img", img.toString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void GetGroup(final int groupId, final GroupCallBack callBack) {
        // Tag used to cancel the request
        String tag_string_req = "req_get_group";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                ((EnvironmentManager) (Services.GetService(EnvironmentManager.class))).GetAPIGetGroupURL(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully pulled from MySQL
                        String id = jObj.getString("id");
                        String name = jObj.getString("name");
                        String createdStr = jObj.getString("created");
                        String img = jObj.getString("img");

                        Date created = new Date(createdStr);

                        Group group = new Group(name);

                        callBack.onSuccess(group);

                    } else {

                        // Error occurred in getting group. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        callBack.onFailure(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailure("JSON ERROR");
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.onFailure("Volley ERROR");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to group url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", groupId + "");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void GetUserTasks(final int userId, final TasksCallBack callBack) {

    }

    public void GetGroupTasks(final int groupId, final GroupCallBack callBack) {

    }

    public void GetUserGroups(final int groupId, final GroupsCallBack callBack) {

    }

    //TODO delete this when finishing testing
    public void test(final ServerCallBack callBack) {
        driver.RunSqlQuery("SELECT * FROM users", new ServerCallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    boolean error = result.getBoolean("error");
                    if (!error) {
                        callBack.onSuccess(new JSONObject().put("res", result.getString("result")));
                    } else {
                        callBack.onFailure(result.getString("error_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String result) {
                callBack.onFailure(result);
            }
        });
    }
}
