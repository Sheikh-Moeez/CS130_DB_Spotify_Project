SELECT 
    Id,
    ISNULL(username, 'No Username') AS username,
    ISNULL(email, 'No Email') AS email,
    ISNULL(password, 'No Password') AS password,
    ISNULL(CAST(DOB AS VARCHAR), 'No DOB') AS DOB,
    ISNULL(subscriptionId, 'No Subscription') AS subscriptionId
FROM Users;
