"use client";

import { Button } from "@nextui-org/react";
import {useTheme} from "next-themes";
import { useState } from "react";
import { MdOutlineLightMode } from "react-icons/md";
import { MdOutlineDarkMode } from "react-icons/md";

export function ThemeSwitcher() {
  const [mounted, setMounted] = useState(true)
  const { theme, setTheme } = useTheme()


  const changeTheme = () => {
    mounted ? setTheme("light") : setTheme("dark")
    setMounted(!mounted)
  }

  return (
    mounted ? (
        <Button className='bg-inherit' variant='solid' size='lg' disableRipple  disableAnimation isIconOnly onClick={changeTheme}><MdOutlineLightMode /></Button>
    ) : (
        <Button className='bg-inherit' variant='solid' size='lg' disableRipple disableAnimation isIconOnly onClick={changeTheme}><MdOutlineDarkMode /></Button>
    )
  )
};