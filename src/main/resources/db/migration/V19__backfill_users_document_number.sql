UPDATE users
   SET document_number = '52998224725'
 WHERE email = 'admin@mechanicalhub.com'
   AND document_number IS NULL;

UPDATE users
   SET document_number = '11144477735'
 WHERE email = 'mecanico@mechanicalhub.com'
   AND document_number IS NULL;
