package space.itoncek.csyt.comm;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CommLibTest {

    @Test
    void getPlayer() throws SQLException{
        CommLib lib = new CommLib("jdbc:mysql://cloud.itoncek.space:3306/s2_proxytest", "u2_J4yDWKHPiC", "MadJcFVUqz^xZw@1LLxycaR^") {};
    }
}