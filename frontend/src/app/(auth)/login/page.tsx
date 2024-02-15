import LoginForm from "@/components/LoginForm";
import { Card, CardBody } from "@nextui-org/react";

export default function Login() {
  return (
    <div className="flex flex-col text-center mt-14 items-center">
      <p className="text-4xl">Log In Below</p>

      <Card className="max-w-full w-[340px] h-[450px] mt-8">
        <CardBody>
          <LoginForm />
        </CardBody>
      </Card>
    </div>
  );
}
