public class TokenFetcher  extends Worker {
    private static final String API_URL = "http://10.135.30.111/api/auth/login";
    private static final String USERNAME = "san.suraj51@gmail.com";
    private static final String PASSWORD = "dZX8xnJVqNGRg1zjziZ+";  // Raw password (not hashed)

    public TokenFetcher(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchToken();
        return Result.success();
    }

    private void fetchToken() {

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("UserName", USERNAME);
            requestData.put("Password", PASSWORD);  // Sending only hashed password
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            saveToken(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TokenFetcher", "Error fetching token: " + error.getMessage());
                    }
                });

        queue.add(jsonRequest);
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

}
