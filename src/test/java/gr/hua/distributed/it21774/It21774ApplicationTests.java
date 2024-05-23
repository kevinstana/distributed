package gr.hua.distributed.it21774;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class It21774ApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@Order(1)
	void contextLoads() {
	}

	@Test
	@Order(2)
	public void createUserTest() throws Exception {

		// Login as admin to get jwt and create a new user
		String adminJson = "{\"username\":\"admin\",\"password\":\"password\"}";

		ResultActions adminResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(adminJson));

		String jwt = adminResult.andReturn().getResponse().getHeader("Authorization");
		adminResult.andExpect(status().isOk());

		// Create the new user
		String userJson = "{\"username\":\"testuser\",\"password\":\"testpassword\",\"email\":\"test@hua.gr\",\"role\":[\"lawyer\"],\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"afm\":\"777777777\",\"amka\":\"77777777777\"}";

		ResultActions userResult = mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson)
				.header("Authorization", jwt));

		userResult.andExpect(status().isOk());
	}

	@Test
	@Order(3)
	public void newUserLoginTest() throws Exception {
		// Login as the new user
		String userLoginJson = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";

		ResultActions userLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userLoginJson));

		userLoginResult.andExpect(status().isOk());
	}
}
