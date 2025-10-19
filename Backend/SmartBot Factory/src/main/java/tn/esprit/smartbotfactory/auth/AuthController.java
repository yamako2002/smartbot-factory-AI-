package tn.esprit.smartbotfactory.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.smartbotfactory.user.*;

@RestController @RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:4200")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository u, PasswordEncoder e, JwtService j){ this.users=u; this.encoder=e; this.jwt=j; }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest req){
        if (users.existsByEmail(req.email)) throw new RuntimeException("Email already used");
        User u = new User(); u.setEmail(req.email); u.setPassword(encoder.encode(req.password)); u.setFullName(req.fullName);
        users.save(u);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req){
        User u = users.findByEmail(req.email).orElseThrow(() -> new RuntimeException("Bad credentials"));
        if(!encoder.matches(req.password, u.getPassword())) throw new RuntimeException("Bad credentials");
        return new AuthResponse(jwt.generate(u.getEmail()));
    }
}
