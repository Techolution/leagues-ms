package com.makeurpicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.makeurpicks.dao.PlayerDao;
import com.makeurpicks.domain.Player;
import com.makeurpicks.domain.PlayerBuilder;
import com.makeurpicks.exception.PlayerValidationException;
import com.makeurpicks.exception.PlayerValidationException.PlayerExceptions;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = AuthServerApplication.class)
//@RunWith(MockitoJUnitRunner.class)
//@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerServiceTest {

	@Autowired
	@InjectMocks
	private PlayerService playerService;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	
	@Mock
	public PlayerDao playerDAOMock;
	
	@Test
	public void createPlayer()
	{
//		Player user = new PlayerBuilder("tim", "tdelesio@gmail.com", "123456").adAdmin().build();
//		user = playerService.createPlayer(user);
//		
//		UserDetails player = playerService.loadUserByUsername(user.getUsername());
//		
//		Assert.assertEquals(user.getUsername(), player.getUsername());
//		
//		Assert.assertTrue(encoder.matches("123456", player.getPassword()));
		
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void validatePlayer_InValidDataNullName_throwsPlayerValidationException()  {
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.USERNAME_IS_NULL.toString());
		Player player=new PlayerBuilder(null, "testuser@gmail.com", "Test123").build();
		playerService.validatePlayer(player);
		
	}
	
	@Test
	public void validatePlayer_InValidDatEmptyName_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.USERNAME_IS_NULL.toString());
		Player player=new PlayerBuilder("", "testuser@gmail.com", "Test123").build();
		playerService.validatePlayer(player);
		
	}
	
	
	@Test
	public void validatePlayer_InValidDataNullEmail_throwsPlayerValidationException()  {
	
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.EMAIL_IS_NULL.toString());
		Player player=new PlayerBuilder("user1", null, "Test123").build();
		playerService.validatePlayer(player);
		
	}
	
	@Test
	public void validatePlayer_InValidDatEmptyEmail_throwsPlayerValidationException()  {

		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.EMAIL_IS_NULL.toString());
		Player player=new PlayerBuilder("user1", "", "Test123").build();
		playerService.validatePlayer(player);
		
	}
	
	@Test
	public void validatePlayer_InValidDataNullPassword_throwsPlayerValidationException()  {
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.PASSWORD_DOES_NOT_MEET_REQ.toString());
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", null).build();
		playerService.validatePlayer(player);
	}
	
	@Test
	public void validatePlayer_InValidDatEmptyPassword_throwsPlayerValidationException()  {
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.PASSWORD_DOES_NOT_MEET_REQ.toString());
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", "").build();
		playerService.validatePlayer(player);
		
	}
	
	@Test
	public void validatePlayer_null_throwsPlayerValidationException()  {
		
		expectedEx.expect(PlayerValidationException.class);
		playerService.validatePlayer(null);
		
	}
	
	
	
	@Test
	public void createPlayer_InValidDataNullName_throwsPlayerValidationException()  {
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.USERNAME_IS_NULL.toString());
		Player player=new PlayerBuilder(null, "testuser@gmail.com", "Test123").build();
		playerService.createPlayer(player);
		
	}
	
	@Test
	public void createPlayer_InValidDatEmptyName_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.USERNAME_IS_NULL.toString());
		Player player=new PlayerBuilder("", "testuser@gmail.com", "Test123").build();
		playerService.createPlayer(player);
	
	}
	
	
	@Test
	public void createPlayer_InValidDataNullEmail_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.EMAIL_IS_NULL.toString());
		Player player=new PlayerBuilder("user1", null, "Test123").build();
		playerService.createPlayer(player);
		
	}
	
	@Test
	public void createPlayer_InValidDatEmptyEmail_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.EMAIL_IS_NULL.toString());
		Player player=new PlayerBuilder("user1", "", "Test123").build();
		playerService.createPlayer(player);
		
	}
	
	@Test
	public void createPlayer_InValidDataNullPassword_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.PASSWORD_DOES_NOT_MEET_REQ.toString());
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", null).build();
		playerService.createPlayer(player);
	
		
		
	}
	
	@Test
	public void createPlayer_InValidDatEmptyPassword_throwsPlayerValidationException()  {
		
		
		expectedEx.expect(PlayerValidationException.class);
		expectedEx.expectMessage(PlayerExceptions.PASSWORD_DOES_NOT_MEET_REQ.toString());
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", "").build();
		playerService.createPlayer(player);
	
		
		
	}
	
	@Test
	public void createPlayer_ValidData_returnsObject()  {
		
		
		String inputPassword="Test123";
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", inputPassword).build();
		playerService.setPasswordEncoder(passwordEncoder());
		Player player2=playerService.createPlayer(player);
		assertNotSame(inputPassword, player2.getPassword());
		verify(playerDAOMock).save(player);
		
	}
	
	@Test
	public void loadByUserName_admin_datareturned(){
		
		
		Player player=new PlayerBuilder("admin1", "testuser@gmail.com", "Test123").adAdmin().build();
		
		when(playerDAOMock.findByUsername("admin1")).thenReturn(player);
		Player player2=(Player)playerService.loadUserByUsername("admin1");
		
		assertThat(player2.getAuthorities().contains("ROLE_ADMIN"));
		assertThat(player2.getAuthorities().contains("ROLE_USER"));
		
		
		
	}
	
	@Test
	public void loadByUserName_Nonadmin_datareturned(){
		
		
		Player player=new PlayerBuilder("user1", "testuser@gmail.com", "Test123").build();
		
		when(playerDAOMock.findByUsername("user1")).thenReturn(player);
		Player player2=(Player)playerService.loadUserByUsername("user1");
		
		assertThat(!(player2.getAuthorities().contains("ROLE_ADMIN")));
		assertThat(player2.getAuthorities().contains("ROLE_USER"));
		
		
		
	}
	
	@Test
	public void loadByUserName_null_nullturned(){
		
		
		Player player2=(Player)playerService.loadUserByUsername(null);
		
		assertNull(player2);
		
		
		
	}

	
}
