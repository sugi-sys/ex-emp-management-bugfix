package com.example.component;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// import com.example.domain.Administrator;
// import com.example.service.AdministratorService;

// @Component
// public class CustomAuthentication implements AuthenticationProvider {
//     @Autowired
// 	private AdministratorService administratorService;

//     @Override
//     public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//         String username = authentication.getName();
//         String password = authentication.getCredentials().toString();

//         // ユーザー情報をデータベース等から取得
//         Administrator admin = administratorService.findByMailAddress();

//         // パスワードをBCryptで検証
//         if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//             return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
//         } else {
//             throw new BadCredentialsException("Invalid username or password");
//         }
//     }

//     @Override
//     public boolean supports(Class<?> authentication) {
//         return authentication.equals(UsernamePasswordAuthenticationToken.class);
//     }
// }
