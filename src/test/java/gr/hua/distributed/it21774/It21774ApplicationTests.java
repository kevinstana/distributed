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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

	// Login as lawyer and create contract
	@Test
	public void c() throws Exception {
		String lawyerLoginJson = "{\"username\":\"lawyer_one\",\"password\":\"password\"}";

		ResultActions lawyerLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(lawyerLoginJson));

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

		ResultActions createContractResult = mockMvc.perform(post("/users/" + userId + "/contract")
				.contentType(MediaType.APPLICATION_JSON)
				.content(contractJson)
				.header("Authorization", jwt));

		createContractResult.andExpect(status().isOk());
	}

	// Login as client one and confirm contract
	@Test
	public void d() throws Exception {
		String clientLoginJson = "{\"username\":\"client_one\",\"password\":\"password\"}";

		ResultActions clientLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(clientLoginJson));

		clientLoginResult.andExpect(status().isOk());

		String jwt = clientLoginResult.andReturn().getResponse().getHeader("Authorization");
		String payload = Jwts.parser()
							.setSigningKey(jwtSecret)
							.parseClaimsJws(jwt.split(" ")[1])
							.getBody()
							.toString();

		String[] payloadToPieces = payload.split(",");
		String[] payloadId = payloadToPieces[1].split("=");
		Long userId = Long.parseLong(payloadId[1]);

		ResultActions answerContractResult = mockMvc.perform(put("/users/" + userId + "/contract")
				.header("Authorization", jwt));

		answerContractResult.andExpect(status().isOk());
	}

	// Login as client two and confirm contract
	@Test
	public void e() throws Exception {
		String clientLoginJson = "{\"username\":\"client_two\",\"password\":\"password\"}";

		ResultActions clientLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(clientLoginJson));

		clientLoginResult.andExpect(status().isOk());

		String jwt = clientLoginResult.andReturn().getResponse().getHeader("Authorization");
		String payload = Jwts.parser()
							.setSigningKey(jwtSecret)
							.parseClaimsJws(jwt.split(" ")[1])
							.getBody()
							.toString();

		String[] payloadToPieces = payload.split(",");
		String[] payloadId = payloadToPieces[1].split("=");
		Long userId = Long.parseLong(payloadId[1]);

		ResultActions answerContractResult = mockMvc.perform(put("/users/" + userId + "/contract")
				.header("Authorization", jwt));

		answerContractResult.andExpect(status().isOk());
	}

	// Login as lawyer one and confirm contract
	@Test
	public void f() throws Exception {
		String lawyerLoginJson = "{\"username\":\"lawyer_one\",\"password\":\"password\"}";

		ResultActions lawyerLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(lawyerLoginJson));

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

		ResultActions answerContractResult = mockMvc.perform(put("/users/" + userId + "/contract")
				.header("Authorization", jwt));

		answerContractResult.andExpect(status().isOk());
	}

	// Login as lawyer two and confirm contract
	@Test
	public void g() throws Exception {
		String lawyerLoginJson = "{\"username\":\"lawyer_two\",\"password\":\"password\"}";

		ResultActions lawyerLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(lawyerLoginJson));

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

		ResultActions answerContractResult = mockMvc.perform(put("/users/" + userId + "/contract")
				.header("Authorization", jwt));

		answerContractResult.andExpect(status().isOk());
	}

	// Login as admin to get jwt and create a new user with role notary
	@Test
	public void h() throws Exception {
		String adminJson = "{\"username\":\"admin\",\"password\":\"password\"}";

		ResultActions adminResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(adminJson));

		String jwt = adminResult.andReturn().getResponse().getHeader("Authorization");
		adminResult.andExpect(status().isOk());

		// Create the new user (with notary role)
		String userJson = "{\"username\":\"testuser\",\"password\":\"testpassword\",\"email\":\"test@hua.gr\",\"role\":[\"notary\"],\"firstName\":\"firstName\",\"lastName\":\"lastName\",\"afm\":\"777777777\",\"amka\":\"77777777777\"}";

		ResultActions userResult = mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userJson)
				.header("Authorization", jwt));

		userResult.andExpect(status().isOk());
	}

	// Login as the new user (notary) and approve the contract
	@Test
	public void i() throws Exception {
		String userLoginJson = "{\"username\":\"testuser\",\"password\":\"testpassword\"}";

		ResultActions userLoginResult = mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userLoginJson));

		String jwt = userLoginResult.andReturn().getResponse().getHeader("Authorization");
		userLoginResult.andExpect(status().isOk());

		ResultActions answerContractResult = mockMvc.perform(put("/contracts/1")
				.header("Authorization", jwt));

		answerContractResult.andExpect(status().isOk());
	}
}
