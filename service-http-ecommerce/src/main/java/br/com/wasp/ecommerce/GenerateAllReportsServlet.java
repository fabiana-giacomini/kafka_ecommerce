package br.com.wasp.ecommerce;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {
    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            batchDispatcher.send("SEND_MESSAGES_TO_ALL_USERS", "SEND_MESSAGES_TO_ALL_USERS", "SEND_MESSAGES_TO_ALL_USERS");

            System.out.println("Sent generate report to all users.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated.");
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }
}
