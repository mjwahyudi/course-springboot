package com.module1.demo4.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {
  private boolean enabled = false;
  private String message = "Hello";
  private String audience = "World";

  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  
  public String getAudience() { return audience; }
  public void setAudience(String audience) { this.audience = audience; }
}
