import {
  Avatar,
  Button,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
} from "@nextui-org/react";

import { useEffect, useState } from "react";
import { UserResponse } from "@/dto/userResponse";
import { logOutAction } from "@/app/lib/authActions";
import { usePathname, useRouter } from "next/navigation";
import { ServerResponse } from "@/dto/errorResponse";
import Link from "next/link";

export default function AvatarComponent(props: { isAuthorized: boolean }) {
  const [dropDownOpen, setDropDownOpen] = useState<boolean>(false);
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(props.isAuthorized);

  const router = useRouter();

  const pathName = usePathname();

  const [userData, setUserData] = useState<UserResponse>({
    username: "",
    email: "",
    credits: 0,
  });

  useEffect(() => {
    setIsLoggedIn(props.isAuthorized);
  }, [props.isAuthorized]);

  const fetchUserData = async (isOpen: boolean) => {
    if (isOpen) {
      const res = await fetch("/api/users");
      if (!res.ok && res.status !== 302) {
        setIsLoggedIn(false);
        router.push("/login");
        return;
      }
      if (res.status === 302) {
        const data = await res.json();

        const userResponse: UserResponse = {
          ...data,
        };

        await setUserData(userResponse);
        setDropDownOpen(true);
        return;
      }

      const serverResponse: ServerResponse = await res.json();
      console.error(serverResponse.message);
    }
  };
  return (
    <>
      {isLoggedIn ? (
        <Dropdown
          placement="bottom-end"
          onOpenChange={(isOpen) => fetchUserData(isOpen)}
          isOpen={dropDownOpen}
          onClose={() => setDropDownOpen(false)}
        >
          <DropdownTrigger>
            <Avatar
              isBordered
              as="button"
              className="transition-transform"
              color="primary"
              size="sm"
              showFallback
            />
          </DropdownTrigger>
          <DropdownMenu aria-label="Profile Actions" variant="flat">
            <DropdownItem
              textValue="accessibility"
              key="profile"
              className="h-14 gap-2"
            >
              <p className="font-semibold">Signed in as</p>
              <p className="font-semibold">{userData.email}</p>
            </DropdownItem>
            <DropdownItem textValue="accessibility" key="credits">
              Credits: {userData.credits}
            </DropdownItem>
            <DropdownItem
              onClick={() => {
                setIsLoggedIn(false);
                logOutAction();
              }}
              key="logout"
              color="danger"
            >
              Log Out
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      ) : (
        <Button
          as={Link}
          color="primary"
          href="/register"
          variant="flat"
          isDisabled={pathName === "/login" || pathName === "/register"}
        >
          Sign Up
        </Button>
      )}
    </>
  );
}
