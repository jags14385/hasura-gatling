import { AuthClient } from '../../e2e/clients/auth-client';
import dotenv from 'dotenv';
import fs from 'fs';

dotenv.config();

const authClient = new AuthClient();
const run = async () => {
  const auth = await authClient.getAuth();
  const jwt = auth.data.id_token;
  const csvData = `authToken\n${jwt}`;
  fs.writeFileSync('perf-tests/src/gatling/resources/token.csv', csvData);
};

run();
