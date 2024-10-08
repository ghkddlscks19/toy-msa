package org.example.userservice.controller;

import org.example.userservice.dto.UserDto;
import org.example.userservice.entity.UserEntity;
import org.example.userservice.service.UserService;
import org.example.userservice.vo.Greeting;
import org.example.userservice.vo.RequestUser;
import org.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;
    private final UserService userService;

    public UserController(Environment env, Greeting greeting, UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"
                +  ", port(local.server.port)=" + env.getProperty("local.server.port")
                +  ", port(server.port)=" + env.getProperty("server.port")
                +  ", port(token.secret)=" + env.getProperty("token.secret")
                +  ", port(token.expiration_time)=" + env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> users = userService.getUserbyAll();

        List<ResponseUser> result = new ArrayList<>();

        users.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto findUser = userService.getUserById(userId);
        ResponseUser result = new ModelMapper().map(findUser, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }
}
