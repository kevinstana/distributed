package gr.hua.distributed.it21774;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import io.jsonwebtoken.Jwts;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class It21774ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Value("${app.jwtSecret}")
    private String jwtSecret;

	@Test
	void contextLoads() {
	}

	// Login as admin to get jwt and create a new user
	@Test
	public void a() throws Exception {
		String adminJson = "{\"username\":\"admin\",\"password\":\"password\"}";

		ResultActions adminResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(adminJson));

		String jwt = adminResult.andReturn().getResponse().getHeader("Authorization");
		adminResult.andExpect(status().isOk());

		// Create the new user (with notary role)
		String userJson = "{\"username\":\"testuser\",\"password\":\"testpassword\",\"email\":\"test@hua.gr\",\"role\":[\"lawyer\"],\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"afm\":\"777777777\",\"amka\":\"77777777777\"}";

		ResultActions userResult = mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson)
				.header("Authorization", jwt));

		userResult.andExpect(status().isOk());
	}

	// Login as the new user
	@Test
	public void b() throws Exception {
		String userLoginJson = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";

		ResultActions userLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userLoginJson));

		userLoginResult.andExpect(status().isOk());
	}

	@Test
	public void c() throws Exception {
		String userLoginJson = "{\"username\":\"lawyer_one\",\"password\":\"password\"}";

		ResultActions lawyerLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userLoginJson));

		lawyerLoginResult.andExpect(status().isOk());

		String jwt = lawyerLoginResult.andReturn().getResponse().getHeader("Authorization");
		String payload = Jwts.parser()
							.setSigningKey(jwtSecret)
							.parseClaimsJws(jwt.split(" ")[1])
							.getBody()
							.toString();

		String[] payloadToPieces = payload.split(",");
		String[] payloadId = payloadToPieces[1].split("=");
		Long userId = Long.parseLong(payloadId[1]);

		String contractJson = "{\"afm\":[\"222222222\",\"333333333\",\"444444444\",\"555555555\"],\"text\":\"This is a test contract created by lawyer_one. Members of the contract are client one, two and lawyers one, two.\"}";

		ResultActions userResult = mockMvc.perform(post("/users/" + userId + "/contract")
				.contentType(MediaType.APPLICATION_JSON)
				.content(contractJson)
				.header("Authorization", jwt));

		userResult.andExpect(status().isOk());
	}
}
