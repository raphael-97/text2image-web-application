"use client";

import { Button } from "@nextui-org/react";
import { useTheme } from "next-themes";
import { useEffect, useLayoutEffect, useState } from "react";
import { MdOutlineLightMode } from "react-icons/md";
import { MdOutlineDarkMode } from "react-icons/md";

export function ThemeSwitcher() {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  const changeTheme = () => {
    theme === "dark" ? setTheme("light") : setTheme("dark");
  };

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <>
        <Button
          className="dark:hidden bg-inherit"
          variant="solid"
          size="lg"
          disableRipple
          disableAnimation
          isIconOnly
        >
          <MdOutlineDarkMode />
        </Button>
        <Button
          className="hidden dark:flex bg-inherit ml-2"
          variant="solid"
          size="lg"
          disableRipple
          disableAnimation
          isIconOnly
        >
          <MdOutlineLightMode />
        </Button>
      </>
    );
  }

  return theme === "dark" ? (
    <Button
      className="bg-inherit"
      variant="solid"
      size="lg"
      disableRipple
      disableAnimation
      isIconOnly
      onClick={changeTheme}
    >
      <MdOutlineLightMode />
    </Button>
  ) : (
    <Button
      className="bg-inherit"
      variant="solid"
      size="lg"
      disableRipple
      disableAnimation
      isIconOnly
      onClick={changeTheme}
    >
      <MdOutlineDarkMode />
    </Button>
  );
}
