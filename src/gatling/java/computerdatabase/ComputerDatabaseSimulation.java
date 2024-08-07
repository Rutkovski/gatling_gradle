package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static org.galaxio.gatling.javaapi.Transactions.endTransaction;
import static org.galaxio.gatling.javaapi.Transactions.startTransaction;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.galaxio.gatling.transactions.Predef;

public class ComputerDatabaseSimulation extends Predef.SimulationWithTransactions {
    static ChainBuilder search = exec(http("Home").get("/"));
    public static ChainBuilder thirdAction =
            exec(
                    startTransaction("transaction1")
            )
                    .exec(
                            session -> {
                                System.out.println("Hello from Gatling!");
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                session.markAsSucceeded();
                                return session;
                            }
                    )
                    .exec(endTransaction("transaction1"));
    HttpProtocolBuilder httpProtocol = http.baseUrl("https://computer-database.gatling.io");
    //    private static final ScenarioBuilder users2 = scenario("Users").during(10).on(pace(1).exec(search,thirdAction));
    private static final ScenarioBuilder users3 = scenario("Users").randomSwitch().on(
            percent(50.0).then(search),
            percent(50.0).then(thirdAction)
    );

    {
        setUp(users3.injectOpen(rampUsersPerSec(1).to(3).during(30),
                constantUsersPerSec(3).during(60)).protocols(httpProtocol));
    }
}