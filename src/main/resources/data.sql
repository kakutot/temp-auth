INSERT INTO oauth_client_details
    (client_id,
     client_secret,
      scope,
       authorized_grant_types,
    web_server_redirect_uri,
     authorities, access_token_validity,
    refresh_token_validity,
     additional_information,
      autoapprove)
VALUES
    ("DEFAULT_CLIENT", "$2y$12$ZJmVyEJHIstjWpsQX.NrzexYu28k3bgsi/H6V2pEl57tpqQqNErFi",
     "TRUSTED_CLIENT,USER_INFO", "password,authorization_code,refresh_token", null, null,
       36000, 36000, null, true);