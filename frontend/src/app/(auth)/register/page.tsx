import {
  Button,
  Card,
  CardBody,
  Input,
  Link,
  Divider,
} from "@nextui-org/react";

export default function Register() {
  return (
    <div className="flex flex-col text-center mt-14 items-center">
      <p className="text-4xl">Sign Up Below</p>

      <Card className="max-w-full w-[340px] h-[500px] mt-8">
        <CardBody>
          <form className="px-5">
            <Input
              className="pt-5"
              label="Name"
              isRequired
              placeholder="Enter your username"
              type="text"
            />
            <Input
              className="pt-5"
              label="Email"
              isRequired
              placeholder="Enter your email"
              type="email"
            />
            <Input
              className="pt-5"
              label="Password"
              isRequired
              placeholder="Enter your password"
              type="password"
            />
            <p className="text-center text-small pt-5">
              Already have an account?{" "}
              <Link href="/login" size="sm">
                Login
              </Link>
            </p>
            <div className="pt-5">
              <Button fullWidth color="secondary">
                Sign up
              </Button>
            </div>

            <div className="flex">
              <Divider className="mt-8" />
            </div>
            <p className="text-center text-small pt-5">or continue with </p>
            <div className="flex flex-row items-center justify-center gap-3 mt-2">
              <Link href="/">
                <img
                  className="dark:hidden"
                  src="web_light_rd_na.svg"
                  alt="Google Logo"
                />
                <img
                  className="hidden dark:flex"
                  src="web_dark_rd_na.svg"
                  alt="Google Logo"
                />
              </Link>
            </div>
          </form>
        </CardBody>
      </Card>
    </div>
  );
}
