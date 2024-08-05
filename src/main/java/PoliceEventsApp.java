import java.awt.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class PoliceEventsApp extends JFrame {
    private final DefaultListModel<String> eventsModel;
    private final String BASE_URL = "https://polisen.se";

    public PoliceEventsApp() {
        setTitle("Police Events");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLUE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        eventsModel = new DefaultListModel<>();
        JList<String> eventsList = new JList<>(eventsModel);
        add(new JScrollPane(eventsList), BorderLayout.CENTER);

        JLabel supportMessage = new JLabel("Support the police!", SwingConstants.CENTER);
        supportMessage.setForeground(Color.WHITE);
        add(supportMessage, BorderLayout.NORTH);
    }

    private void fetchEvents() {
        try {
            JSONArray events = getEvents();
            for (int i = 0; i < events.length(); i++) {
                JSONObject event = events.getJSONObject(i);
                String dateTime = event.getString("datetime");
                String name = event.getString("name");
                String urlFromApi = event.getString("url");
                String type = event.getString("type");
                JSONObject jsonObject = event.getJSONObject("location");
                String cityName = jsonObject.get("name").toString();
                String gpsCoordinates = jsonObject.get("gps").toString();
                eventsModel.addElement(parseDateTimeString(dateTime) + ": " + name + ": " + type + ": " + "Url: " + BASE_URL + urlFromApi + ": " + "City: " + cityName + ": " + "GPS: " + gpsCoordinates);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parseDateTimeString(String dateTime){
      return dateTime.split("\\+")[0];
    }

    private JSONArray getEvents() throws IOException {
        final URL url = new URL(BASE_URL + "/api/events/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder sb = new StringBuilder();

        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        conn.disconnect();

        return new JSONArray(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PoliceEventsApp app = new PoliceEventsApp();
            app.setVisible(true);
            app.fetchEvents();
        });
    }
}