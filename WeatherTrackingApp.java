import java.sql.*;
import java.util.Scanner;

public class WeatherTrackingApp {
    private static Scanner scanner = new Scanner(System.in);

    // Load JDBC Driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            while (true) {
                System.out.println("Welcome to the Weather Forecasting System");
                System.out.println("1. Signup");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        signup();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice! Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Database connection method
    public static Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/WeatherDB";
        String username = "your_username"; // Replace with your MySQL username
        String password = "your_password"; // Replace with your MySQL password

        return DriverManager.getConnection(url, username, password);
    }

    // Signup method
    public static void signup() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (Connection conn = connect()) {
            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    System.out.println("Signup successful! Your user ID is: " + userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Login method
    public static void login() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try (Connection conn = connect()) {
            String query = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    System.out.println("Login successful! Your user ID is: " + userId);
                    manageWeatherData(userId);
                } else {
                    System.out.println("Invalid credentials!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Manage weather data for logged-in user
    public static void manageWeatherData(int userId) {
        System.out.println("1. View Weather Data");
        System.out.println("2. Forecast Weather");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewWeatherData();
                break;
            case 2:
                forecastWeather();
                break;
            case 3:
                System.out.println("Exiting...");
                return;
            default:
                System.out.println("Invalid choice! Try again.");
        }
    }

    // Method to view weather data
    public static void viewWeatherData() {
        try (Connection conn = connect()) {
            String query = "SELECT * FROM Weather_Data";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                System.out.println("Weather Data:");
                while (rs.next()) {
                    System.out.println("Date: " + rs.getString("date"));
                    System.out.println("City: " + rs.getString("city"));
                    System.out.println("Temperature: " + rs.getDouble("temperature") + "°C");
                    System.out.println("Humidity: " + rs.getDouble("humidity") + "%");
                    System.out.println("Rainfall: " + rs.getDouble("rainfall") + "mm");
                    System.out.println("-----");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to forecast weather
    public static void forecastWeather() {
        System.out.print("Enter the city for forecasting: ");
        String city = scanner.nextLine();

        try (Connection conn = connect()) {
            String query = "SELECT * FROM Weather_Data WHERE city = ? ORDER BY date DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, city);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double temperature = rs.getDouble("temperature");
                    double humidity = rs.getDouble("humidity");
                    double rainfall = rs.getDouble("rainfall");

                    System.out.println("Latest Weather Data for " + city + ":");
                    System.out.println("Temperature: " + temperature + "°C");
                    System.out.println("Humidity: " + humidity + "%");
                    System.out.println("Rainfall: " + rainfall + "mm");

                    System.out.println("\nForecast for tomorrow:");
                    System.out.println("Temperature: " + (temperature + 2) + "°C");
                    System.out.println("Humidity: " + (humidity + 5) + "%");
                    System.out.println("Rainfall: " + (rainfall + 10) + "mm");
                } else {
                    System.out.println("No data available for city: " + city);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
