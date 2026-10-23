package com.icbc.reminder;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Full-context smoke test: needs a running MySQL and Redis, so it is skipped in the
// default build. The unit tests in this package mock every external dependency.
@Disabled("Requires local MySQL and Redis")
@SpringBootTest
class ReminderApplicationTests {

	@Test
	void contextLoads() {
	}

}
