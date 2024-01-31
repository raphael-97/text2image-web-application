import RegisterForm from "@/components/RegisterForm";
import { Card, CardBody } from "@nextui-org/react";

export default async function Register() {
  return (
    <div className="flex flex-col text-center mt-14 items-center">
      <p className="text-4xl">Sign Up Below</p>

      <Card className="max-w-full w-[340px] h-[500px] mt-8">
        <CardBody>
          <RegisterForm />
        </CardBody>
      </Card>
    </div>
  );
}
