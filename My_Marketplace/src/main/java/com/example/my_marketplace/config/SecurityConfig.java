package com.example.my_marketplace.config;

import com.example.my_marketplace.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig{
    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }

    // Задаем конфигурацию для работу Spring Security:
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // отключаем защиту от межсайтовой подделки запросов
                // .csrf().disable()

                // Все страницы должны быть защищены аутентификацией
                .authorizeHttpRequests()

                // Страница /admin доступна пользователям с ролью ADMIN
                .requestMatchers("/admin").hasRole("ADMIN")

                // Разрешаем заходить на перечисленные страницы НЕ аутентифицированным пользователям
                // Также здесь даем доступ к папкам, хранящим ресурсы(картинки, стили, скрипты и прочее)
                .requestMatchers("/authentication", "/registration", "/error", "/resources/**", "/static/**", "/img/**", "/product", "/product/info/{id}", "/product/search").permitAll()

                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and() // указываем что дальше настраиватеся аутентификация и соединяем ее с настройкой доступа
                .formLogin().loginPage("/authentication") // указываем какой url запрос будет отправлятся при заходе на защищенные страницы
                .loginProcessingUrl("/process_login") // указываем на какой адрес будут отправляться данные с формы. Нам уже не нужно будет создавать метод в контроллере и обрабатывать данные с формы. Мы задали url, который используется по умолчанию для обработки формы аутентификации по средствам Spring Security. Spring Security будет ждать объект с формы аутентификации и затем сверять логин и пароль с данными в БД
                .defaultSuccessUrl("/person account", true) // Указываем на какой url необходимо направить пользователя после успешной аутентификации. Вторым аргументом указывается true чтобы перенаправление шло в любом случае послу успешной аутентификации
                .failureUrl("/authentication?error") // Указываем куда необходимо перенаправить пользователя при проваленной аутентификации. В запрос будет передан объект error, который будет проверятся на форме и при наличии данного объекта в запросе выводится сообщение "Неправильный логин или пароль"
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        return http.build();
    }

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }
}